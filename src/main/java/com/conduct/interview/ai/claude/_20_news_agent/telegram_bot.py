"""HTTP-обгортка Telegram Bot API + зберігання підписників у JSON-файлі."""

import json
import logging
import os
from pathlib import Path

import requests

log = logging.getLogger("news_agent.telegram")

HERE = Path(__file__).resolve().parent
SUBSCRIBERS_FILE = HERE / "subscribers.json"
TELEGRAM_TOKEN_FILE = HERE / "telegram_token.txt"

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
    """True якщо токен бота налаштований."""
    return TOKEN is not None


_bot_username: str | None = None


def get_bot_username() -> str | None:
    """@username бота (кешується на час процесу)."""
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
