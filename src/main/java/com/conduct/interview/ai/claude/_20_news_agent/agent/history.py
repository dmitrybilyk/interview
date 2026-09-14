"""Лог класифікованих новин за тиждень для /digest.

RSS показує лише ~80 останніх новин (кілька годин). Щоб /digest міг відповісти
"що було за 7 днів" — broadcaster записує кожен запуск сюди.
"""

import json
import logging
import time

from .config import HISTORY_FILE, MAX_HISTORY_AGE_SECONDS

log = logging.getLogger("news_agent.history")


def _load() -> dict:
    if HISTORY_FILE.exists():
        return json.loads(HISTORY_FILE.read_text())
    return {}


def _save(history: dict) -> None:
    HISTORY_FILE.write_text(json.dumps(history, ensure_ascii=False, indent=2))


def record(mood: str, items: list[dict]) -> None:
    """Записує items у лог. Вже існуючий item зберігає свій first_seen."""
    history = _load()
    now = time.time()

    for it in items:
        entry = history.setdefault(it["link"], {
            "title": it["title"],
            "description": it["description"],
            "link": it["link"],
            "category": it.get("category"),
            "moods": {},
            "first_seen": now,
        })
        entry["moods"][mood] = True
        if it.get("category"):
            entry["category"] = it["category"]

    cutoff = now - MAX_HISTORY_AGE_SECONDS
    before = len(history)
    history = {link: e for link, e in history.items() if e["first_seen"] >= cutoff}
    if len(history) != before:
        log.info("Pruned %d history entr(y/ies) older than the retention window", before - len(history))

    _save(history)
    log.info("Recorded %d item(s) under mood=%s (history now holds %d item(s))", len(items), mood, len(history))


def query(mood: str, hours: float) -> list[dict]:
    """Повертає записи за mood за останні hours годин, найновіші першими."""
    history = _load()
    cutoff = time.time() - hours * 3600
    matches = [
        e for e in history.values()
        if e["moods"].get(mood) and e["first_seen"] >= cutoff
    ]
    matches.sort(key=lambda e: e["first_seen"], reverse=True)
    return matches
