"""
The "agent" package — split into one file per concern so each can be read
and changed independently:

    config.py     settings: which provider, which model, which files
    fetch.py      perception: RSS -> plain dicts
    moods.py      policy: what counts as a match, in plain language
    providers.py  reasoning: call_llm(prompt) -> str, Claude or Groq
    classify.py   orchestration: fetch + providers + moods -> filtered list,
                  with a cache so repeat items never get re-classified

See the repo's README.md ("What each file teaches") for a guided tour, and
LEARNING.md for how this connects to the other claude/ lessons.

Everything below is just re-exporting the small public surface so callers
(app.py, broadcaster.py, cli.py) can do `from agent import get_filtered_news`
without knowing which internal file it lives in.
"""

from . import feedback, history
from .classify import get_categories, get_filtered_news, pick_by_mood
from .fetch import fetch_items
from .moods import CATEGORY_LABELS, MOOD_RULES
from .providers import call_llm

__all__ = [
    "get_filtered_news",
    "get_categories",
    "pick_by_mood",
    "fetch_items",
    "MOOD_RULES",
    "CATEGORY_LABELS",
    "call_llm",
    "history",
    "feedback",
]
