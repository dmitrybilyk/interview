"""
HISTORY — a rolling window of past classified items, so /digest can answer
"what happened in the last N hours/days" even though fetch.py's live feed
only ever exposes the site's current ~80-item RSS window (a handful of
hours' worth at this site's post volume — never a full week). There is no
external archive to query instead, so this module builds one: every
broadcaster.py tick (see its systemd timer in deploy.sh) records whatever
it just classified, and /digest reads the accumulated file instead of the
live feed.

Same "plain JSON file, not a database" reasoning as classify.py's cache:
small, and shared between processes by just reading the same file.
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
    """Remember that `items` were seen under `mood` right now. An item
    already in history keeps its original first_seen, so its age is
    measured from when it first appeared on the feed, not from every later
    tick it's still sitting there."""
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
    """Entries seen under `mood` within the last `hours`, newest first."""
    history = _load()
    cutoff = time.time() - hours * 3600
    matches = [
        e for e in history.values()
        if e["moods"].get(mood) and e["first_seen"] >= cutoff
    ]
    matches.sort(key=lambda e: e["first_seen"], reverse=True)
    return matches
