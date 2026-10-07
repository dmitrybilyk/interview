"""IT-новини з DOU.ua, AIN.ua, Хабр і міжнародних джерел — завантаження і AI-фільтрація.

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

TECH_RSS_SOURCES = [
    ("dou",         "https://dou.ua/lenta/news/feed/",                      30),
    ("ain",         "https://ain.ua/feed/",                                  30),
    ("hn",          "https://hnrss.org/frontpage",                           30),
    ("theverge",    "https://www.theverge.com/rss/index.xml",                30),
    ("arstechnica", "https://feeds.arstechnica.com/arstechnica/index",       30),
    ("techcrunch",  "https://techcrunch.com/feed/",                          30),
]
TECH_FETCH_LIMIT = 80
TECH_CACHE_FILE  = APP_DIR / "tech_cache.json"
_HEADERS = {"User-Agent": "Mozilla/5.0 (compatible; news-agent/1.0)"}

TECH_RULES = (
    "You are filtering tech news from multiple sources (Ukrainian and international). "
    "The goal: keep only POSITIVE, inspiring, or exciting tech news. "
    "Be strict. Most items should be REJECTED.\n\n"

    "STEP 1 — REJECT immediately if ANY of these is true:\n"
    "L0. The article is written in Russian. "
    "Russian signals: 'который', 'они', 'нужно', 'можно', 'после', 'против', 'своей', "
    "'будет', 'стал', 'был', 'это', 'также', 'только', 'когда', 'между'. → REJECT.\n"
    "L1. NEGATIVE content: hacks, breaches, leaks, cyberattacks, data theft, outages, "
    "failures, layoffs, fines, lawsuits, bans, scandals, vulnerabilities, zero-days, "
    "ransomware, phishing, fraud, monopoly abuse, privacy violations. → REJECT.\n"
    "L2. Job ads, hiring, vacancies, career advice, salary surveys, courses, tutorials, "
    "certifications, meetup announcements, podcasts, opinion pieces without concrete news. → REJECT.\n"
    "L3. Startup funding rounds under $100M, minor product updates, app version bumps. → REJECT.\n"
    "L4. Generic business news, market reports, company profiles, acquisitions for cost-cutting. → REJECT.\n\n"

    "STEP 2 — KEEP only if passed STEP 1 AND clearly matches ONE of:\n"

    "K1. JAVA / JVM ECOSYSTEM — positive releases: new Java/JDK major version, "
    "major Spring / Quarkus / Micronaut release, important JVM performance improvements, "
    "major JavaOne / Devoxx announcements.\n\n"

    "K2. AI / LLM — exciting breakthroughs: major new model releases (GPT-5, Claude 4, "
    "Gemini 3, Llama 4, etc.), AI research that solves a hard problem ('first ever', "
    "'surpasses human at X'), impressive open-source releases (DeepSeek, Mistral), "
    "genuinely cool AI demos or capabilities.\n"
    "DO NOT keep: minor updates, AI tips, chatbot releases, regulation/safety concerns.\n\n"

    "K3. INSPIRING TECH — stories that make tech people excited: viral GitHub projects "
    "(100k+ stars), amazing new tools or frameworks, hardware breakthroughs "
    "(new CPU/GPU architecture, quantum computing milestone), impressive open-source "
    "projects, 'robot does X for first time', surprising positive benchmarks, "
    "new programming language major version.\n\n"

    "K4. SIGNIFICANT POSITIVE RELEASES — major open-source milestones: Linux kernel major, "
    "PostgreSQL/MySQL major, Kubernetes major, Node.js LTS, browser engine new feature, "
    "major acquisitions that expand capabilities (>$5B, clearly positive for developers).\n\n"

    "K5. UKRAINE IT — positive only: Ukrainian developers winning competitions, "
    "Ukrainian tech product reaching global fame, major tech company investing in Ukraine.\n\n"

    "If in doubt → REJECT. Default is to drop."
)


def _fetch_tech(limit: int = TECH_FETCH_LIMIT) -> list[dict]:
    all_items = []
    seen_links: set[str] = set()
    for source_id, url, src_limit in TECH_RSS_SOURCES:
        try:
            log.info("Fetching tech RSS from %s (limit=%d)", source_id, src_limit)
            resp = requests.get(url, headers=_HEADERS, timeout=10)
            resp.raise_for_status()
            root = ET.fromstring(resp.content)
            for item in root.findall("./channel/item")[:src_limit]:
                link = (item.findtext("link") or "").strip()
                if not link or link in seen_links:
                    continue
                seen_links.add(link)
                all_items.append({
                    "title":       (item.findtext("title")       or "").strip(),
                    "link":        link,
                    "description": (item.findtext("description") or "").strip(),
                    "pubDate":     (item.findtext("pubDate")     or "").strip(),
                    "source":      source_id,
                })
        except Exception:
            log.exception("Failed to fetch tech RSS from %s", source_id)
    log.info("Fetched %d tech items total from %d sources", len(all_items), len(TECH_RSS_SOURCES))
    return all_items


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
