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

# Дешевий префільтр: у LLM ідуть лише новини, що згадують потрібні теми.
# Все інше відкидаємо без виклику моделі — економить ліміт Groq і прибирає шум.
SPORTS_KEYWORDS = (
    "шахтар", "гірник",
    "світолін", "костюк",
    "збірн",  # збірна України — що саме з неї лишити, вирішує LLM
)

SPORTS_RULES = (
    "You pick ONLY the most important sports news for a Ukrainian reader. "
    "Keep an item only if it is significant news about one of these topics:\n"
    "T1. ФК Шахтар Донецьк — match results and reports, previews of Champions League / "
    "European / top УПЛ matches, confirmed transfers, head-coach decisions, serious injuries "
    "of key players, interviews of the coach or Шахтар players about the team.\n"
    "T2. Еліна Світоліна or Марта Костюк — match results, tournament progress, ranking "
    "milestones, injuries or withdrawals, their own comments about tennis.\n"
    "T3. Ukraine senior MEN's national team in football, basketball or volleyball — "
    "squad announcements, match results, qualification news.\n\n"
    "REJECT: betting previews ('прогноз і анонс'), live-stream links ('дивитися онлайн'), "
    "opinions of journalists, experts or ex-players, agent rumours, personal life, photo "
    "galleries, politics, youth / U-21 / women's teams, futsal, other Ukrainian or foreign "
    "clubs (even with Ukrainian players), Russian-language text. If in doubt → REJECT."
)


def _matches_keywords(item: dict) -> bool:
    text = f"{item['title']} {item['description']}".lower()
    return any(k in text for k in SPORTS_KEYWORDS)


def _fetch_sports(limit: int = SPORTS_FETCH_LIMIT) -> list[dict]:
    log.info("Fetching sports RSS (limit=%d): %s", limit, SPORTS_RSS_URL)
    # sport.ua кешує RSS на своєму боці: без унікального параметра віддає фід, що завис годинами
    resp = requests.get(SPORTS_RSS_URL, params={"_": int(time.time())}, headers=_HEADERS, timeout=10)
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


def _classify_batch(batch: list[dict]) -> set[int] | None:
    """Повертає set 0-based індексів items для зберігання, або None якщо LLM не дав відповіді."""
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
    # gpt-oss іноді витрачає весь max_tokens на роздуми і повертає порожній content.
    # Це не "нічого не підходить" — не кешуємо, спробуємо наступного запуску.
    if not re.search(r"\[[\d,\s]*\]", text):
        log.warning("sports: LLM returned no JSON array (%d chars) — will retry next run", len(text))
        return None
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
    for it in unknown:
        if not _matches_keywords(it):
            cache[it["link"]] = False
    unknown = [it for it in unknown if it["link"] not in cache]

    if unknown:
        log.info("sports: %d new item(s) to classify", len(unknown))
        for start in range(0, len(unknown), CLASSIFY_BATCH_SIZE):
            batch = unknown[start:start + CLASSIFY_BATCH_SIZE]
            log.info("  sports batch %d-%d of %d...", start, start + len(batch) - 1, len(unknown))
            kept = _classify_batch(batch)
            if kept is None:
                continue
            for i, it in enumerate(batch):
                cache[it["link"]] = i in kept
            if start + CLASSIFY_BATCH_SIZE < len(unknown):
                time.sleep(3)
        log.info("sports: classification done, kept %d/%d new item(s)",
                 sum(1 for it in unknown if cache.get(it["link"])), len(unknown))
    _save_cache(cache)  # навіть без LLM — префільтр теж записав рішення

    result = [it for it in items if cache.get(it["link"]) is True]
    log.info("sports: returning %d item(s)", len(result))
    return result
