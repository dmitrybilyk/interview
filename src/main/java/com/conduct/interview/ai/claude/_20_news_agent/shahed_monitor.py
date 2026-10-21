"""
Моніторинг Telegram-каналу на слова-тригери (райони прильотів шахедів).
Запускається як userbot — логіниться під твоїм акаунтом.

Налаштування:
  1. Отримай API_ID і API_HASH на https://my.telegram.org → App configuration
  2. Заповни CONFIG нижче
  3. pip install telethon
  4. python shahed_monitor.py
     (перший запуск запитає номер телефону + код — збереже сесію у shahed.session)
"""

import asyncio
import logging
import re

from telethon import TelegramClient, events
import shahed_config

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)-7s %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("shahed_monitor")

# ── Налаштування ──────────────────────────────────────────────────────────────

CONFIG = {
    "api_id":     shahed_config.API_ID,
    "api_hash":   shahed_config.API_HASH,
    "channel":    "@lvivych_news",
    "my_chat_id": 1091195927,
}

# Слова-тригери — назви твоїх районів (регістр не важливий)
KEYWORDS = [
    # Львів — райони
    "шевченківськ",
    "залізничн",
    "знесіння",
    "личаківськ",

    # Приміські села — північ/захід
    "брюховичі",
    "брюховицьк",
    "рясне",
    "рясне-руськ",
    "левандівк",
    "білогорщ",
    "зимна вода",
    "суховол",
    "рудно",
    "залужж",
    "повітно",
    "мшана",
    "паланки",
    "воротів",
    "домажир",
    "кожичі",
    "карачинів",
    "солуки",
    "підрясне",
    "мальчиці",
    "ямельня",
    "поріччя",
    "великополе",
    "івано-франков",
    "страдч",
    "зелів",
    "жорниськ",

    # Схід від Львова
    "малі грибовичі",
    "великі грибовичі",
    "бірки",
    "збіранка",
    "яснівськ",
    "лісопоток",
    "воля-гомулецьк",
    "кам'янка",
]

# ─────────────────────────────────────────────────────────────────────────────

PATTERN = re.compile(
    "|".join(re.escape(k) for k in KEYWORDS),
    flags=re.IGNORECASE,
)


def _matched_keywords(text: str) -> list[str]:
    return list({m.group(0).lower() for m in PATTERN.finditer(text)})


async def main() -> None:
    client = TelegramClient(
        "shahed.session",
        CONFIG["api_id"],
        CONFIG["api_hash"],
    )

    await client.start()
    log.info("Підключено. Слухаю канал %s ...", CONFIG["channel"])

    @client.on(events.NewMessage(chats=CONFIG["channel"]))
    async def handler(event: events.NewMessage.Event) -> None:
        text = event.raw_text or ""
        matched = _matched_keywords(text)
        if not matched:
            return

        log.info("Тригер: %s | %s", matched, text[:80])

        alert = (
            "⚠️ ШАХЕДИ — твій район\n\n"
            f"Знайдено: {', '.join(matched)}\n\n"
            f"{text}"
        )
        await client.send_message(CONFIG["my_chat_id"], alert)

    await client.run_until_disconnected()


if __name__ == "__main__":
    if not CONFIG["api_id"] or not CONFIG["api_hash"]:
        print(
            "\nНалаштуй CONFIG у файлі:\n"
            "  api_id    — https://my.telegram.org → App configuration\n"
            "  api_hash  — там само\n"
            "  channel   — username каналу (@...)\n"
            "  my_chat_id — свій chat_id (надішли будь-що боту @userinfobot)\n"
        )
    else:
        asyncio.run(main())
