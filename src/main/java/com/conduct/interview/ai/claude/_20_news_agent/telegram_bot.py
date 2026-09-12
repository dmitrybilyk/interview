"""
Thin wrapper around the Telegram Bot API (plain HTTP — no extra dependency)
plus the subscriber list, stored as a flat JSON file: {chat_id: {"mood": ...}}.

Not a bot framework — just the handful of calls this app needs:
sending a message, answering a button press, and reading/writing subscribers.
"""

import json
import logging
import os
from pathlib import Path

import requests

log = logging.getLogger("news_agent.telegram")

HERE = Path(__file__).resolve().parent
SUBSCRIBERS_FILE = HERE / "subscribers.json"
# Unlike key.txt / groq_key.txt, this token is specific to this one project
# (not shared with the other claude/ lessons), so it only ever lives here,
# next to app.py — same path locally and once deployed.
TELEGRAM_TOKEN_FILE = HERE / "telegram_token.txt"

# Shared by both delivery surfaces (app.py's /start + /donate, and
# broadcaster.py's periodic reminder) — defined once here so they can't drift.
DONATE_CARD = "4441 1110 3446 3160"  # Monobank
DONATE_LINE = f"☕ Розробнику на каву, якщо бот подобається (Monobank): {DONATE_CARD}"
DONATE_REMINDER_INTERVAL_SECONDS = 30 * 24 * 60 * 60  # once a month, see broadcaster.py


def _load_token() -> str | None:
    if os.environ.get("TELEGRAM_BOT_TOKEN"):
        return os.environ["TELEGRAM_BOT_TOKEN"]
    if TELEGRAM_TOKEN_FILE.exists():
        return TELEGRAM_TOKEN_FILE.read_text().strip()
    return None


TOKEN = _load_token()
API = f"https://api.telegram.org/bot{TOKEN}" if TOKEN else None


def is_configured() -> bool:
    """False when no token is set up — callers use this to hide the
    subscribe button / skip the broadcaster entirely, instead of crashing."""
    return TOKEN is not None


_bot_username: str | None = None


def get_bot_username() -> str | None:
    """Cached lookup of the bot's @username, used to build the t.me link
    shown on the page. Only hits the network once per process lifetime."""
    global _bot_username
    if not is_configured():
        return None
    if _bot_username is None:
        resp = requests.get(f"{API}/getMe", timeout=10)
        resp.raise_for_status()
        _bot_username = resp.json()["result"]["username"]
        log.info("Resolved bot username: @%s", _bot_username)
    return _bot_username


def load_subscribers() -> dict:
    if SUBSCRIBERS_FILE.exists():
        return json.loads(SUBSCRIBERS_FILE.read_text())
    return {}


def save_subscribers(subscribers: dict) -> None:
    SUBSCRIBERS_FILE.write_text(json.dumps(subscribers, ensure_ascii=False, indent=2))


def send_message(chat_id, text: str, reply_markup: dict | None = None) -> None:
    log.info("Sending Telegram message to chat_id=%s (%d chars)", chat_id, len(text))
    payload = {"chat_id": chat_id, "text": text, "parse_mode": "HTML"}
    if reply_markup:
        payload["reply_markup"] = reply_markup
    resp = requests.post(f"{API}/sendMessage", json=payload, timeout=10)
    if not resp.ok:
        log.warning("sendMessage to chat_id=%s failed: %s", chat_id, resp.text[:300])


def answer_callback_query(callback_query_id: str, text: str | None = None) -> None:
    payload = {"callback_query_id": callback_query_id}
    if text:
        payload["text"] = text
    requests.post(f"{API}/answerCallbackQuery", json=payload, timeout=10)
