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

SPORTS_RSS_URL   = "https://sport.ua/rss/all"
SPORTS_FETCH_LIMIT = 100
SPORTS_CACHE_FILE  = APP_DIR / "sports_cache.json"
_HEADERS = {"User-Agent": "Mozilla/5.0 (compatible; news-agent/1.0)"}

SPORTS_RULES = (
    "You are filtering Ukrainian sports news. Keep ONLY items that match one of these topics.\n\n"

    "STEP 1 — REJECT immediately if ANY of these is true:\n"
    "L0. The article is written in Russian (not Ukrainian). "
    "Russian signals: 'Смотреть', 'который', 'они', 'нужно', 'сборной', 'матча', 'игроки', "
    "'будет', 'можно', 'после', 'против', 'своей', 'место', 'очень', 'стал', 'был', 'это'. "
    "→ REJECT immediately, do not check topics.\n"
    "Also REJECT if none of the topics below apply.\n\n"

    "STEP 2 — KEEP only if NOT rejected above AND clearly matches ONE of:\n"
    "K1. ФУТБОЛ — будь-які футбольні новини: УПЛ, єврокубки (ЛЧ, ЛЄ, ЛК), збірна України, "
    "українські клуби (Шахтар, Динамо, Металіст, Дніпро-1 тощо), трансфери, результати матчів, "
    "прев'ю, таблиці, тренерські призначення в українських клубах.\n"
    "Signals: будь-які назви українських клубів, 'УПЛ', 'збірна', 'Ліга чемпіонів', 'Ліга Європи', "
    "'ЛЧ', 'ЛЄ', 'Champions League', 'Europa League', футбол, гол, матч, турнір.\n\n"

    "K2. ТЕНІС — Elina Svitolina (Світоліна) or Marta Kostyuk (Костюк) in any tournament: "
    "match result, draw, ranking, tournament progress. Either player qualifies.\n"
    "Signals: 'Світоліна', 'Svitolina', 'Костюк', 'Kostyuk'.\n\n"

    "K3. ВОЛЕЙБОЛ — Ukraine MEN's national volleyball team in OFFICIAL competitions: "
    "World Championship, European Championship, Olympic qualification, Nations League. "
    "NOT club volleyball, NOT women's team.\n"
    "Signals: 'збірна України', 'волейбол', 'чоловіча збірна', 'ЧС', 'ЧЄ'.\n\n"

    "K4. БАСКЕТБОЛ — Ukraine MEN's national basketball team in FIBA official competitions: "
    "EuroBasket, World Cup, Olympic qualification. NOT club basketball.\n"
    "Signals: 'збірна України', 'баскетбол', 'FIBA', 'EuroBasket'.\n\n"

    "K5. ХОКЕЙ — Ukraine MEN's national ice hockey team in official competitions: "
    "World Championship, Olympic qualification. NOT club hockey.\n"
    "Signals: 'збірна України', 'хокей', 'ЧС'.\n\n"

    "K6. SOCCA — Ukraine MEN's national Socca (small-sided football) team in official competitions.\n"
    "Signals: 'Socca', 'збірна України', 'соккер'.\n\n"

    "K7. ФУТЗАЛ — Ukraine MEN's national futsal team in official competitions: "
    "UEFA Futsal Euro, World Cup, qualification. NOT club futsal.\n"
    "Signals: 'збірна України', 'футзал', 'міні-футбол'.\n\n"

    "K8. ЛЕГКА АТЛЕТИКА — Major athletics tournaments (Diamond League, World Championships, "
    "European Championships, Olympics, World Indoors) featuring Ukrainian athletes, "
    "or notable records/achievements by Ukrainians.\n"
    "Signals: 'легка атлетика', 'Діамантова ліга', 'Diamond League', 'чемпіонат світу', "
    "'чемпіонат Європи', Ukrainian athlete names, 'рекорд', 'медаль'.\n\n"

    "REJECT: бокс, боротьба, велоспорт, веслування, жіночі команди (крім тенісу), "
    "іноземні клуби без участі українців, спонсорські матеріали.\n\n"

    "If in doubt → REJECT."
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
