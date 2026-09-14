"""Telegram webhook: команди (/start /mood /digest), кнопки, дайджест."""

import logging
import time
from collections import Counter
from urllib.parse import quote

import telegram_bot
from agent import CATEGORY_LABELS, feedback, history
from agent.classify import _load_classification_cache, _save_classification_cache

log = logging.getLogger("news_agent.tg")

DIGEST_WINDOWS = {
    24:      "за останні 24 години",
    24 * 7:  "за останні 7 днів",
}

START_TEXT = (
    "Привіт! Обери, які новини України надсилати:\n\n"
    "✅ Позитивні — лише хороші для України: втрати окупантів, удари по рф, "
    "проблеми економіки росії. Відбираються ШІ.\n"
    "🌤 Здебільшого позитивні — позитивні + нейтральні. Без явно поганих. Теж ШІ.\n"
    "📰 Усі — без фільтрації, без AI.\n\n"
    "🔄 Змінити вибір: /mood\n"
    "📊 Дайджест: /digest\n\n"
) + telegram_bot.DONATE_LINE

SWITCH_TEXT = "Обери новий настрій — зміна набуде чинності одразу:"

MOOD_CONFIRM_TEXT = {
    "positive":       "Готово! Лише позитивні новини (втрати ворога, удари по рф, проблеми росії), відібрані ШІ.",
    "mostly_positive": "Готово! Позитивні та нейтральні — без українських втрат і просування ворога. ШІ.",
    "all":            "Готово! Усі новини без фільтрації.",
}


def build_start_keyboard() -> dict:
    rows = [
        [{"text": "✅ Позитивні",              "callback_data": "mood:positive"}],
        [{"text": "🌤 Здебільшого позитивні",  "callback_data": "mood:mostly_positive"}],
        [{"text": "📰 Усі новини",             "callback_data": "mood:all"}],
    ]
    username = telegram_bot.get_bot_username()
    if username:
        link = f"https://t.me/{username}"
        share_url = f"https://t.me/share/url?url={quote(link)}&text={quote('Новини України за настроєм 🇺🇦')}"
        rows.append([{"text": "📤 Поділитися ботом", "url": share_url}])
    rows.append([{"text": "🛑 Відписатися", "callback_data": "mood:stop"}])
    return {"inline_keyboard": rows}


def build_digest_keyboard() -> dict:
    return {"inline_keyboard": [
        [{"text": "🕐 За 24 години", "callback_data": "digest:24"}],
        [{"text": "🗓 За 7 днів",    "callback_data": "digest:168"}],
    ]}


def _format_digest(mood: str, hours: float, entries: list[dict]) -> str:
    period = DIGEST_WINDOWS.get(int(hours), f"за останні {hours:.0f} год")
    if not entries:
        return (
            f"{period.capitalize()} новин під твій настрій ще не назбиралось.\n\n"
            "Дайджест будується поступово (раз на 15 хв) — зачекай трохи."
        )

    lines = [f"📊 Дайджест {period} ({len(entries)} новин):"]

    if mood == "all":
        lines.append("")
        for e in entries[:10]:
            lines.append(f"• {e['title']}\n  {e['link']}")
        if len(entries) > 10:
            lines.append(f"\n…і ще {len(entries) - 10}. Повна стрічка: /mood")
        return "\n".join(lines)

    counts = Counter(e.get("category") or "other" for e in entries)
    lines.append("")
    for cat in ("strike", "losses", "economy", "other"):
        if counts.get(cat):
            lines.append(f"{CATEGORY_LABELS.get(cat, '🔹 Інше')}: {counts[cat]}")

    for cat in ("strike", "losses", "economy"):
        batch = [e for e in entries if e.get("category") == cat][:5]
        if not batch:
            continue
        lines.append(f"\n{CATEGORY_LABELS[cat]}:")
        for e in batch:
            lines.append(f"• {e['title']}\n  {e['link']}")

    text = "\n".join(lines)
    if len(text) > 3800:  # Telegram hard cap — 4096
        text = text[:3800].rsplit("\n", 1)[0] + "\n\n…(обрізано)"
    return text


def handle(update: dict) -> str:
    """Обробляє один Telegram update. Повертає 'ok'."""
    message = update.get("message")
    if message:
        text    = message.get("text", "")
        chat_id = message["chat"]["id"]

        if text.startswith("/start"):
            log.info("/start from chat_id=%s", chat_id)
            telegram_bot.send_message(chat_id, START_TEXT, reply_markup=build_start_keyboard())
        elif text.startswith("/mood"):
            log.info("/mood from chat_id=%s", chat_id)
            telegram_bot.send_message(chat_id, SWITCH_TEXT, reply_markup=build_start_keyboard())
        elif text.startswith("/donate"):
            telegram_bot.send_message(chat_id, telegram_bot.DONATE_LINE)
        elif text.startswith("/digest"):
            log.info("/digest from chat_id=%s", chat_id)
            telegram_bot.send_message(chat_id, "За який період дайджест?", reply_markup=build_digest_keyboard())
        return "ok"

    cq = update.get("callback_query")
    if cq:
        chat_id = cq["message"]["chat"]["id"]
        data    = cq.get("data", "")
        log.info("button chat_id=%s data=%s", chat_id, data)
        subscribers = telegram_bot.load_subscribers()

        if data.startswith("rep:"):
            h    = data.split(":", 1)[1]
            item = feedback.load_sent_items().get(h)
            if not item:
                telegram_bot.answer_callback_query(cq["id"], "Не знайдено — можливо, застаріло.")
                return "ok"
            rule  = feedback.add_feedback(item["link"], item["title"], item["description"])
            cache = _load_classification_cache()
            cache.pop(item["link"], None)
            _save_classification_cache(cache)
            telegram_bot.answer_callback_query(cq["id"], "Дякую! Враховано.")
            log.info("feedback rule=%r", rule)

        elif data.startswith("digest:"):
            hours = float(data.split(":", 1)[1])
            sub   = subscribers.get(str(chat_id))
            if not sub:
                telegram_bot.answer_callback_query(cq["id"])
                telegram_bot.send_message(chat_id, "Спочатку обери настрій через /start.")
                return "ok"
            entries = history.query(sub["mood"], hours)
            telegram_bot.answer_callback_query(cq["id"])
            telegram_bot.send_message(chat_id, _format_digest(sub["mood"], hours, entries))

        elif data == "mood:stop":
            subscribers.pop(str(chat_id), None)
            telegram_bot.save_subscribers(subscribers)
            telegram_bot.answer_callback_query(cq["id"], "Відписано")
            telegram_bot.send_message(chat_id, "Більше не надсилатиму. Напиши /start щоб підписатися знову.")
            log.info("chat_id=%s unsubscribed", chat_id)

        elif data.startswith("mood:"):
            mood     = data.split(":", 1)[1]
            existing = subscribers.get(str(chat_id), {"last_donate_reminder": time.time()})
            existing["mood"] = mood
            subscribers[str(chat_id)] = existing
            telegram_bot.save_subscribers(subscribers)
            telegram_bot.answer_callback_query(cq["id"], "Підписано!")
            telegram_bot.send_message(chat_id, MOOD_CONFIRM_TEXT[mood] + "\n\n🔄 /mood — змінити будь-коли")
            log.info("chat_id=%s mood=%s", chat_id, mood)

    return "ok"
