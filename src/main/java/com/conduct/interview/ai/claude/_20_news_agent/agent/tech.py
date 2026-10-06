"""IT-новини з DOU.ua — завантаження і AI-фільтрація.

Самодостатній модуль (власний fetch, кеш, правила) — легко виокремити.
"""

import json
import logging
import re
import time
import xml.etree.ElementTree as ET

import requests

from .config import APP_DIR, CLASSIFY_BATCH_SIZE
from .providers import call_llm

log = logging.getLogger("news_agent.tech")

TECH_RSS_URL    = "https://dou.ua/lenta/news/feed/"
TECH_FETCH_LIMIT = 50
TECH_CACHE_FILE  = APP_DIR / "tech_cache.json"
_HEADERS = {"User-Agent": "Mozilla/5.0 (compatible; news-agent/1.0)"}

TECH_RULES = (
    "You are filtering Ukrainian IT news from DOU.ua. Keep ONLY items that match one of these.\n\n"

    "STEP 1 — REJECT immediately if ANY of these is true:\n"
    "L0. The article is written in Russian (not Ukrainian). "
    "Russian signals: 'который', 'они', 'нужно', 'можно', 'после', 'против', 'своей', "
    "'будет', 'стал', 'был', 'это', 'также', 'только', 'когда', 'между'. → REJECT.\n"
    "Also REJECT if none of the topics below apply.\n\n"

    "STEP 2 — KEEP only if the item clearly matches ONE of:\n"

    "K1. JAVA — significant news about Java ecosystem: new Java/JDK version release, "
    "major Spring Framework / Spring Boot / Quarkus / Micronaut release or announcement, "
    "important Jakarta EE / JVM news, notable Java performance findings, "
    "major Java conferences (JavaOne, Devoxx) announcements or key talks.\n"
    "DO NOT keep: minor library updates, job posts, Java tutorials, Java basics articles.\n\n"

    "K2. AI / LLM — truly notable AI news: major new model releases (GPT-5, Claude 4, Gemini 2, "
    "Llama 4, etc.), significant AI breakthroughs or research results, major AI product launches "
    "(new GPT features, Copilot major updates, etc.), AI regulation or policy news with wide impact, "
    "genuinely remarkable AI demos or capabilities. "
    "DO NOT keep: minor AI tool updates, AI productivity tips, listicles, 'AI for beginners' posts, "
    "every new chatbot release, vague 'AI is changing everything' opinion pieces.\n\n"

    "K3. SIGNIFICANT IT INDUSTRY NEWS — major events only: large tech company layoffs (1000+ people), "
    "major acquisitions (>$1B), significant open-source project releases (Linux kernel, Kubernetes, "
    "PostgreSQL major version), important cybersecurity incidents (large breaches, critical 0-day), "
    "new programming language major version (Python 4, Rust 2.0, Go 2, etc.).\n"
    "DO NOT keep: minor product updates, startup funding rounds, conference announcements, "
    "developer surveys, opinion pieces.\n\n"

    "K4. UKRAINE IT — significant news about Ukrainian IT industry specifically: "
    "major Ukrainian IT company news, Ukrainian developer achievements at international competitions, "
    "IT education initiatives in Ukraine, tech companies investing in or leaving Ukraine.\n"
    "DO NOT keep: generic Ukrainian job market stats, salary surveys.\n\n"

    "REJECT: job ads, tutorials, courses, career advice, salary surveys, company profiles, "
    "minor product updates, opinion pieces, podcasts, meetup announcements.\n\n"

    "If in doubt → REJECT."
)


def _fetch_tech(limit: int = TECH_FETCH_LIMIT) -> list[dict]:
    log.info("Fetching tech RSS (limit=%d): %s", limit, TECH_RSS_URL)
    resp = requests.get(TECH_RSS_URL, headers=_HEADERS, timeout=10)
    resp.raise_for_status()
    root = ET.fromstring(resp.content)
    items = []
    for item in root.findall("./channel/item")[:limit]:
        items.append({
            "title":       (item.findtext("title")       or "").strip(),
            "link":        (item.findtext("link")        or "").strip(),
            "description": (item.findtext("description") or "").strip(),
            "pubDate":     (item.findtext("pubDate")     or "").strip(),
            "source":      "dou.ua",
        })
    log.info("Fetched %d tech items", len(items))
    return items


def _load_cache() -> dict:
    if TECH_CACHE_FILE.exists():
        return json.loads(TECH_CACHE_FILE.read_text())
    return {}


def _save_cache(cache: dict) -> None:
    TECH_CACHE_FILE.write_text(json.dumps(cache, ensure_ascii=False, indent=2))


def _extract_indices(text: str) -> list[int]:
    match = re.search(r"\[[\d,\s]*\]", text)
    if not match:
        return []
    try:
        return json.loads(match.group(0))
    except json.JSONDecodeError:
        return []


def _classify_batch(batch: list[dict]) -> set[int]:
    numbered = "\n".join(
        f"{i + 1}. {it['title']} — {it['description'][:200]}"
        for i, it in enumerate(batch)
    )
    prompt = (
        f"{TECH_RULES}\n\n"
        f"Numbered list:\n{numbered}\n\n"
        "Return ONLY a JSON array of 1-based indices to KEEP. Example: [1,3]. "
        "Empty array [] if nothing matches."
    )
    text = call_llm(prompt)
    indices = _extract_indices(text)
    return {i - 1 for i in indices if 1 <= i <= len(batch)}


def get_tech_news() -> list[dict]:
    """Завантажує DOU.ua і повертає лише відфільтровані IT-новини."""
    items = _fetch_tech()
    if not items:
        return []

    cache = _load_cache()
    current_links = {it["link"] for it in items}

    stale = [k for k in list(cache) if k not in current_links]
    for k in stale:
        del cache[k]

    unknown = [it for it in items if it["link"] not in cache]

    if unknown:
        log.info("tech: %d new item(s) to classify", len(unknown))
        for start in range(0, len(unknown), CLASSIFY_BATCH_SIZE):
            batch = unknown[start:start + CLASSIFY_BATCH_SIZE]
            log.info("  tech batch %d-%d of %d...", start, start + len(batch) - 1, len(unknown))
            kept = _classify_batch(batch)
            for i, it in enumerate(batch):
                cache[it["link"]] = i in kept
            if start + CLASSIFY_BATCH_SIZE < len(unknown):
                time.sleep(3)
        log.info("tech: classification done, kept %d/%d new item(s)",
                 sum(1 for it in unknown if cache.get(it["link"])), len(unknown))
        _save_cache(cache)

    result = [it for it in items if cache.get(it["link"]) is True]
    log.info("tech: returning %d item(s)", len(result))
    return result
