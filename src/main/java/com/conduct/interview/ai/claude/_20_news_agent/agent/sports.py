"""Спортивні новини з sport.ua — завантаження і AI-фільтрація.

Модуль навмисно самодостатній (власний fetch, кеш, правила) щоб легко
виокремити в окремий сервіс. Залежить тільки від config.py і providers.py.
"""

import json
import logging
import re
import time
import xml.etree.ElementTree as ET

import requests

from .config import APP_DIR, CLASSIFY_BATCH_SIZE
from .providers import call_llm

log = logging.getLogger("news_agent.sports")

SPORTS_RSS_URL   = "https://sport.ua/uk/rss/all"
SPORTS_FETCH_LIMIT = 100
SPORTS_CACHE_FILE  = APP_DIR / "sports_cache.json"
_HEADERS = {"User-Agent": "Mozilla/5.0 (compatible; news-agent/1.0)"}

SPORTS_RULES = (
    "You are filtering Ukrainian sports news for a Telegram channel. "
    "Apply rules strictly in order.\n\n"

    "STEP 1 — REJECT immediately (do not check topics) if ANY is true:\n"
    "L1. The text contains Russian language. "
    "Russian signals (any single word is enough): "
    "'который', 'они', 'нужно', 'можно', 'после', 'против', 'своей', 'будет', 'стал', 'был', "
    "'это', 'также', 'только', 'когда', 'между', 'сборной', 'матча', 'игроки', 'место', "
    "'очень', 'смотреть', 'жена', 'муж', 'день рождения', 'поздравил'. → REJECT.\n"
    "L2. The article is about personal life, family, relationships, birthdays, celebrations, "
    "lifestyle, fashion, social media posts of athletes or their relatives. → REJECT.\n"
    "L3. The article is a photo gallery, gossip, or celebrity content (дружина, чоловік, "
    "романтика, сюрприз, особисте, сім'я гравця, іменини). → REJECT.\n\n"

    "STEP 2 — KEEP only if passed STEP 1 AND matches ONE of:\n"
    "K1. ФУТБОЛ — match results, standings, previews, tactics, transfers, coach/player "
    "interviews ABOUT FOOTBALL (not personal life), official squad announcements, "
    "injuries affecting upcoming matches. Ukrainian clubs (Шахтар, Динамо, Металіст, "
    "Дніпро-1, Ворскла etc.) or Ukraine national team in any competition.\n\n"

    "K2. ТЕНІС — Svitolina (Світоліна) or Kostyuk (Костюк): match results, rankings, "
    "tournament draws, AND interviews/press conferences where they speak about tennis, "
    "training, upcoming tournaments, their career. NOT personal/lifestyle news.\n\n"

    "K3. ВОЛЕЙБОЛ / БАСКЕТБОЛ / ХОКЕЙ / ФУТЗАЛ — Ukraine national MEN's teams "
    "in official competitions only (World/European Championships, Olympic qualifiers).\n\n"

    "K4. ЛЕГКА АТЛЕТИКА — Ukrainian athletes at Diamond League, World/European "
    "Championships, Olympics. Results, records, medals, AND interviews with well-known "
    "Ukrainian athletics athletes about competitions, preparation, achievements.\n\n"

    "K5. ІНТЕРВ'Ю ВІДОМИХ УКРАЇНСЬКИХ СПОРТСМЕНІВ — interviews or significant quotes "
    "from: Шахтар players (будь-який гравець Шахтаря), Світоліна, Костюк, відомі "
    "українські легкоатлети — BUT ONLY when they speak about sport, competition, "
    "training, career goals. "
    "Signals: 'інтерв'ю', 'розповів', 'зізнався', 'прокоментував', 'заявив' + athlete name. "
    "NOT personal life, NOT family topics.\n\n"

    "REJECT everything else. If in doubt → REJECT."
)


def _fetch_sports(limit: int = SPORTS_FETCH_LIMIT) -> list[dict]:
    log.info("Fetching sports RSS (limit=%d): %s", limit, SPORTS_RSS_URL)
    resp = requests.get(SPORTS_RSS_URL, headers=_HEADERS, timeout=10)
    resp.raise_for_status()
    root = ET.fromstring(resp.content)
    items = []
    for item in root.findall("./channel/item")[:limit]:
        items.append({
            "title":       (item.findtext("title")       or "").strip(),
            "link":        (item.findtext("link")        or "").strip(),
            "description": (item.findtext("description") or "").strip(),
            "pubDate":     (item.findtext("pubDate")     or "").strip(),
            "source":      "sport.ua",
        })
    log.info("Fetched %d sports items", len(items))
    return items


def _load_cache() -> dict:
    if SPORTS_CACHE_FILE.exists():
        return json.loads(SPORTS_CACHE_FILE.read_text())
    return {}


def _save_cache(cache: dict) -> None:
    SPORTS_CACHE_FILE.write_text(json.dumps(cache, ensure_ascii=False, indent=2))


def _extract_indices(text: str) -> list[int]:
    match = re.search(r"\[[\d,\s]*\]", text)
    if not match:
        return []
    try:
        return json.loads(match.group(0))
    except json.JSONDecodeError:
        return []


def _classify_batch(batch: list[dict]) -> set[int]:
    """Повертає set 0-based індексів items для зберігання."""
    numbered = "\n".join(
        f"{i + 1}. {it['title']} — {it['description'][:200]}"
        for i, it in enumerate(batch)
    )
    prompt = (
        f"{SPORTS_RULES}\n\n"
        f"Numbered list:\n{numbered}\n\n"
        "Return ONLY a JSON array of 1-based indices to KEEP. Example: [1,3]. "
        "Empty array [] if nothing matches."
    )
    text = call_llm(prompt)
    indices = _extract_indices(text)
    return {i - 1 for i in indices if 1 <= i <= len(batch)}


def get_sports_news() -> list[dict]:
    """Завантажує sport.ua і повертає лише відфільтровані новини."""
    items = _fetch_sports()
    if not items:
        return []

    cache = _load_cache()
    current_links = {it["link"] for it in items}

    stale = [k for k in list(cache) if k not in current_links]
    for k in stale:
        del cache[k]

    unknown = [it for it in items if it["link"] not in cache]

    if unknown:
        log.info("sports: %d new item(s) to classify", len(unknown))
        for start in range(0, len(unknown), CLASSIFY_BATCH_SIZE):
            batch = unknown[start:start + CLASSIFY_BATCH_SIZE]
            log.info("  sports batch %d-%d of %d...", start, start + len(batch) - 1, len(unknown))
            kept = _classify_batch(batch)
            for i, it in enumerate(batch):
                cache[it["link"]] = i in kept
            if start + CLASSIFY_BATCH_SIZE < len(unknown):
                time.sleep(3)
        log.info("sports: classification done, kept %d/%d new item(s)",
                 sum(1 for it in unknown if cache.get(it["link"])), len(unknown))
        _save_cache(cache)

    result = [it for it in items if cache.get(it["link"]) is True]
    log.info("sports: returning %d item(s)", len(result))
    return result
