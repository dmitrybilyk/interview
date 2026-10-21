"""
Моніторинг публічного Telegram-каналу через веб-перегляд (без авторизації).
Парсить https://t.me/s/<channel> раз на хвилину, шукає ключові слова,
надсилає алерт через існуючий бот.

Запуск: venv/bin/python shahed_poll.py
"""

import json
import logging
import re
import time
from pathlib import Path

import requests
from bs4 import BeautifulSoup

import telegram_bot

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)-7s %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("shahed_poll")

CHANNEL      = "lvivych_news"
POLL_SECONDS = 15
STATE_FILE   = Path("shahed_poll_state.json")
URL          = f"https://t.me/s/{CHANNEL}"

KEYWORDS = [
    # TEST — розкоментувати для перевірки
    # "львів", "львов", "lviv",

    "шевченківськ", "залізничн", "знесіння", "личаківськ",
    "брюховичі", "брюховицьк", "рясне", "левандівк",
    "білогорщ", "зимна вода", "суховол", "рудно",
    "залужж", "повітно", "мшана", "паланки", "воротів",
    "домажир", "кожичі", "карачинів", "солуки", "підрясне",
    "мальчиці", "ямельня", "поріччя", "великополе",
    "івано-франков", "страдч", "зелів", "жорниськ",
    "малі грибовичі", "великі грибовичі", "бірки", "збіранка",
    "яснівськ", "лісопоток", "воля-гомулецьк", "кам'янка",
]

PATTERN = re.compile(
    "|".join(re.escape(k) for k in KEYWORDS),
    flags=re.IGNORECASE,
)


def load_seen() -> set[str]:
    if STATE_FILE.exists():
        return set(json.loads(STATE_FILE.read_text()))
    return set()


def save_seen(seen: set[str]) -> None:
    STATE_FILE.write_text(json.dumps(list(seen)))


def fetch_messages() -> list[dict]:
    try:
        r = requests.get(URL, timeout=10, headers={"User-Agent": "Mozilla/5.0"})
        r.raise_for_status()
    except Exception as e:
        log.warning("fetch failed: %s", e)
        return []

    soup = BeautifulSoup(r.text, "html.parser")
    messages = []
    for el in soup.select(".tgme_widget_message"):
        msg_id = el.get("data-post", "")
        text_el = el.select_one(".tgme_widget_message_text")
        text = text_el.get_text(" ", strip=True) if text_el else ""
        if msg_id and text:
            messages.append({"id": msg_id, "text": text})
    return messages


def matched_keywords(text: str) -> list[str]:
    return list({m.group(0).lower() for m in PATTERN.finditer(text)})


def main() -> None:
    log.info("Старт — слухаю @%s кожні %ds", CHANNEL, POLL_SECONDS)
    seen = load_seen()

    # При першому запуску — просто запам'ятати поточні пости, не надсилати алерти
    if not seen:
        log.info("Перший запуск — запам'ятовую існуючі пости без надсилання")
        messages = fetch_messages()
        seen = {m["id"] for m in messages}
        save_seen(seen)
        log.info("Запам'ятовано %d постів", len(seen))

    while True:
        time.sleep(POLL_SECONDS)
        messages = fetch_messages()
        new = [m for m in messages if m["id"] not in seen]

        for msg in new:
            seen.add(msg["id"])
            kw = matched_keywords(msg["text"])
            if not kw:
                continue

            log.info("Тригер: %s | %s", kw, msg["text"][:80])
            alert = (
                f"⚠️ ШАХЕДИ — твій район\n\n"
                f"Знайдено: {', '.join(kw)}\n\n"
                f"{msg['text']}\n\n"
                f"https://t.me/{CHANNEL}/{msg['id'].split('/')[-1]}"
            )
            subscribers = telegram_bot.load_subscribers()
            shahed_subs = [
                chat_id for chat_id, sub in subscribers.items()
                if sub.get("shahed")
            ]
            log.info("Надсилаю %d підписникам", len(shahed_subs))
            for chat_id in shahed_subs:
                telegram_bot.send_message(int(chat_id), alert)

        if new:
            save_seen(seen)


if __name__ == "__main__":
    main()
