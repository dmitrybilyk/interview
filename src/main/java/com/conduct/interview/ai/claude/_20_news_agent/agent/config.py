"""Всі налаштування агента в одному місці."""

import os
from pathlib import Path

# LLM провайдер. На сервері deploy.sh передає LLM_PROVIDER через systemd Environment=
# і він перекриває DEFAULT_PROVIDER — зміна цього рядка не впливає на продакшн.
DEFAULT_PROVIDER = "groq"
PROVIDER = os.environ.get("LLM_PROVIDER", DEFAULT_PROVIDER).lower()

CLAUDE_MODEL = "claude-haiku-4-5-20251001"
GROQ_MODEL   = os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b")

# Розмір батчу для класифікації. Reasoning-моделі витрачають приховані токени на
# роздуми — великий батч може вичерпати бюджет і повернути порожній контент.
CLASSIFY_BATCH_SIZE = 20

RSS_URL       = "https://assets.censor.net/rss/censor.net/rss_uk_news.xml"
FETCH_HEADERS = {"User-Agent": "Mozilla/5.0 (compatible; news-agent/1.0)"}

# Шляхи
PACKAGE_DIR       = Path(__file__).resolve().parent   # agent/
APP_DIR           = PACKAGE_DIR.parent                # _20_news_agent/
CLAUDE_LESSONS_DIR = APP_DIR.parent                   # claude/

# Ключі шукаємо спочатку поруч з app.py, потім на рівень вище (спільний key.txt)
KEY_FILE_CANDIDATES      = [APP_DIR / "key.txt",      CLAUDE_LESSONS_DIR / "key.txt"]
GROQ_KEY_FILE_CANDIDATES = [APP_DIR / "groq_key.txt", CLAUDE_LESSONS_DIR / "groq_key.txt"]

# Файли стану (не видаляються при деплої)
CLASSIFICATION_CACHE_FILE = APP_DIR / "classification_cache.json"
FEEDBACK_RULES_FILE       = APP_DIR / "feedback_rules.json"
SENT_ITEMS_FILE           = APP_DIR / "sent_items.json"
HISTORY_FILE              = APP_DIR / "history.json"
MAX_HISTORY_AGE_SECONDS   = 8 * 24 * 60 * 60  # трохи більше тижня
