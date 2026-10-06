"""Пакет агента. Публічне API для app.py, broadcaster.py, cli.py."""

from . import feedback, history
from .classify import get_categories, get_filtered_news, pick_by_mood
from .fetch import fetch_items
from .moods import CATEGORY_LABELS, MOOD_RULES
from .providers import call_llm
from .sports import get_sports_news
from .tech import get_tech_news

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
    "get_sports_news",
    "get_tech_news",
]
