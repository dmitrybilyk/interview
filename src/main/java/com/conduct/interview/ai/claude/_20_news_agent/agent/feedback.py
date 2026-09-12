"""
FEEDBACK — user-reported misclassifications that teach the classifier.

When a Telegram subscriber taps "🚩 Не позитивна" under a news item, this
module:
  1. Generates a short rejection rule (via LLM) that generalises the pattern
  2. Persists it in feedback_rules.json
  3. Exposes load_feedback_rules() so classify.py can inject these rules into
     future prompts — no moods.py edits needed

Sent-item registry (sent_items.json): the broadcaster saves each item's
title/description/link keyed by a short hash so app.py can look up the full
item from a compact callback_data string.
"""

import json
import logging

from .config import FEEDBACK_RULES_FILE, SENT_ITEMS_FILE
from .providers import call_llm

log = logging.getLogger("news_agent.feedback")

MAX_SENT_ITEMS = 500

_RULE_SCHEMA = {
    "type": "object",
    "properties": {"rule": {"type": "string"}},
    "required": ["rule"],
    "additionalProperties": False,
}


def item_hash(link: str) -> str:
    import hashlib
    return hashlib.md5(link.encode()).hexdigest()[:8]


def load_feedback_rules() -> list[str]:
    if not FEEDBACK_RULES_FILE.exists():
        return []
    data = json.loads(FEEDBACK_RULES_FILE.read_text())
    return [e["rule"] for e in data if e.get("rule")]


def load_sent_items() -> dict:
    if SENT_ITEMS_FILE.exists():
        return json.loads(SENT_ITEMS_FILE.read_text())
    return {}


def _save_sent_items(items: dict) -> None:
    SENT_ITEMS_FILE.write_text(json.dumps(items, ensure_ascii=False, indent=2))


def record_sent_item(link: str, title: str, description: str) -> str:
    """Save item metadata keyed by its short hash. Returns the hash."""
    h = item_hash(link)
    items = load_sent_items()
    items[h] = {"title": title, "description": description, "link": link}
    if len(items) > MAX_SENT_ITEMS:
        keys = list(items.keys())
        items = {k: items[k] for k in keys[-MAX_SENT_ITEMS:]}
    _save_sent_items(items)
    return h


def add_feedback(link: str, title: str, description: str) -> str:
    """Generate a rejection rule from this reported item, persist it, and
    return the rule string."""
    prompt = (
        "A Ukrainian news reader reported this item as incorrectly included in the "
        "'positive news for Ukraine' feed.\n\n"
        f"Title: {title}\n"
        f"Description: {description}\n\n"
        "In ONE short phrase (under 20 words), describe the CATEGORY of news this "
        "represents so we can reject similar items in future.\n"
        "Format: 'News about [pattern]' or 'Items where [condition]'.\n"
        "Be specific enough to catch similar items but not so broad that unrelated "
        "news gets blocked. Reply with ONLY the rule — no quotes, no explanation."
    )
    raw = call_llm(prompt, schema=_RULE_SCHEMA)
    try:
        rule = json.loads(raw).get("rule", "").strip()
    except (json.JSONDecodeError, AttributeError):
        rule = raw.strip().strip('"')

    if not rule:
        rule = f"News similar to: {title[:60]}"

    data = []
    if FEEDBACK_RULES_FILE.exists():
        data = json.loads(FEEDBACK_RULES_FILE.read_text())
    data.append({"title": title, "description": description, "link": link, "rule": rule})
    FEEDBACK_RULES_FILE.write_text(json.dumps(data, ensure_ascii=False, indent=2))

    log.info("Feedback rule added: %s", rule)
    return rule
