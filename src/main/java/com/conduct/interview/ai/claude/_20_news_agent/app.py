"""
Tiny Flask UI around the agent/ package, plus a Telegram webhook for
subscribing to a mood (see telegram_bot.py / broadcaster.py for the
sending side).

This file and broadcaster.py are the "delivery" layer — two different
surfaces (a web page, a Telegram push) that both just call
agent.get_filtered_news(mood). Neither knows or cares how that decision
gets made; see agent/ for that.

Run locally:
    python3 -m venv venv && venv/bin/pip install -r requirements.txt
    venv/bin/python app.py
Then open http://localhost:8600 — logs print to this terminal as requests
come in (fetch/cache/LLM steps), since HOST/PORT default to running in
the foreground for local dev.
"""

import logging
import os
import time
from collections import Counter
from urllib.parse import quote

from flask import Flask, render_template, request

import telegram_bot
from agent import CATEGORY_LABELS, feedback, get_filtered_news, history
from agent.classify import _load_classification_cache, _save_classification_cache

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)-7s [%(name)s] %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("news_agent.app")

app = Flask(__name__)

MOODS = ("positive", "mostly_positive", "all")

def build_start_keyboard() -> dict:
    """Same mood buttons every time, plus a "share" button whose target URL
    needs the bot's own @username — built fresh per call (cheap: the
    username itself is cached in telegram_bot.get_bot_username(), so this
    is no extra network call after the first) rather than baked into a
    module-level constant, since we don't know the username at import time.
    """
    rows = [
        [{"text": "✅ Позитивні", "callback_data": "mood:positive"}],
        [{"text": "🌤 Здебільшого позитивні", "callback_data": "mood:mostly_positive"}],
        [{"text": "📰 Усі новини", "callback_data": "mood:all"}],
    ]

    bot_username = telegram_bot.get_bot_username()
    if bot_username:
        # "url" (not "callback_data") opens Telegram's native share sheet
        # client-side — no webhook handling needed for this button at all.
        bot_link = f"https://t.me/{bot_username}"
        share_text = "Підбірка новин України, відфільтрована за настроєм 🇺🇦"
        share_url = f"https://t.me/share/url?url={quote(bot_link)}&text={quote(share_text)}"
        rows.append([{"text": "📤 Поділитися ботом", "url": share_url}])

    rows.append([{"text": "🛑 Відписатися", "callback_data": "mood:stop"}])
    return {"inline_keyboard": rows}


# hours -> (callback suffix, button label, text shown in the digest header)
DIGEST_WINDOWS = {
    24: "за останні 24 години",
    24 * 7: "за останні 7 днів",
}


def build_digest_keyboard() -> dict:
    return {
        "inline_keyboard": [
            [{"text": "🕐 За 24 години", "callback_data": "digest:24"}],
            [{"text": "🗓 За 7 днів", "callback_data": "digest:168"}],
        ]
    }


def _format_digest(mood: str, hours: float, entries: list[dict]) -> str:
    period = DIGEST_WINDOWS.get(int(hours), f"за останні {hours:.0f} год")

    if not entries:
        return (
            f"{period.capitalize()} новин під твій настрій ще не назбиралось.\n\n"
            "Дайджест будується поступово, з кожної перевірки стрічки (раз на "
            "15 хв) — якщо бот увімкнув цю функцію нещодавно, зачекай трохи."
        )

    lines = [f"📊 Дайджест {period} ({len(entries)} новин):"]

    if mood == "all":
        # No categories for mood="all" (see classify.get_filtered_news) — a
        # breakdown would be meaningless, so just list the most recent ones.
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
            label = CATEGORY_LABELS.get(cat, "🔹 Інше")
            lines.append(f"{label}: {counts[cat]}")

    for cat in ("strike", "losses", "economy"):
        cat_entries = [e for e in entries if e.get("category") == cat][:5]
        if not cat_entries:
            continue
        lines.append(f"\n{CATEGORY_LABELS[cat]}:")
        for e in cat_entries:
            lines.append(f"• {e['title']}\n  {e['link']}")

    text = "\n".join(lines)
    # Telegram's hard cap is 4096 chars — stay well under it rather than
    # let a big backlog produce a send that Telegram just rejects outright.
    if len(text) > 3800:
        text = text[:3800].rsplit("\n", 1)[0] + "\n\n…(обрізано, забагато новин)"
    return text

# "Positive"/"negative" are from a Ukrainian reader's point of view, not a
# generic mood — spell that out so it's never ambiguous what someone is
# subscribing to (see agent/moods.py for the exact rules behind each).
START_TEXT = (
    "Привіт! Обери, які новини України надсилати:\n\n"
    "✅ Позитивні — лише хороші новини ДЛЯ УКРАЇНИ: втрати окупантів, удари по "
    "території РФ, проблеми в економіці Росії. Відбираються ШІ.\n"
    "🌤 Здебільшого позитивні — позитивні + нейтральні новини. Без явно поганих "
    "(без українських втрат і просування ворога). Теж ШІ.\n"
    "📰 Усі — без фільтрації, без AI.\n\n"
    "🔄 Змінити вибір можна будь-коли командою /mood.\n"
    "📊 /digest — дайджест новин за 24 год або 7 днів.\n\n"
) + telegram_bot.DONATE_LINE

SWITCH_TEXT = "Обери новий настрій — зміна набуде чинності одразу:"

MOOD_CONFIRM_TEXT = {
    "positive": "Готово! Надсилатиму лише позитивні новини для України (втрати ворога, удари по РФ, проблеми Росії), відібрані ШІ.",
    "mostly_positive": "Готово! Надсилатиму позитивні та нейтральні новини — без українських втрат і просування ворога. Відбираються ШІ.",
    "all": "Готово! Надсилатиму усі новини без фільтрації, щойно з'являться нові.",
}


@app.route("/", methods=["GET"])
def index():
    mood = request.args.get("mood")
    news = None
    error = None

    if mood in MOODS:
        log.info("Page request for mood=%s", mood)
        try:
            news = get_filtered_news(mood)
        except Exception as exc:  # noqa: BLE001 - show any failure to the user
            log.exception("Failed to get filtered news for mood=%s", mood)
            error = str(exc)

    bot_username = telegram_bot.get_bot_username() if telegram_bot.is_configured() else None

    return render_template(
        "index.html", mood=mood, news=news, error=error, bot_username=bot_username
    )


@app.route("/telegram-webhook", methods=["POST"])
def telegram_webhook():
    update = request.get_json(silent=True) or {}

    message = update.get("message")
    if message:
        text = message.get("text", "")
        chat_id = message["chat"]["id"]

        if text.startswith("/start"):
            log.info("Telegram /start from chat_id=%s", chat_id)
            telegram_bot.send_message(chat_id, START_TEXT, reply_markup=build_start_keyboard())
            return "ok"

        if text.startswith("/mood"):
            log.info("Telegram /mood from chat_id=%s", chat_id)
            telegram_bot.send_message(chat_id, SWITCH_TEXT, reply_markup=build_start_keyboard())
            return "ok"

        if text.startswith("/donate"):
            telegram_bot.send_message(chat_id, telegram_bot.DONATE_LINE)
            return "ok"

        if text.startswith("/digest"):
            log.info("Telegram /digest from chat_id=%s", chat_id)
            telegram_bot.send_message(
                chat_id, "За який період дайджест?", reply_markup=build_digest_keyboard()
            )
            return "ok"

    callback_query = update.get("callback_query")
    if callback_query:
        chat_id = callback_query["message"]["chat"]["id"]
        data = callback_query.get("data", "")
        log.info("Telegram button press from chat_id=%s: %s", chat_id, data)
        subscribers = telegram_bot.load_subscribers()

        if data.startswith("rep:"):
            h = data.split(":", 1)[1]
            sent = feedback.load_sent_items()
            item = sent.get(h)
            if not item:
                telegram_bot.answer_callback_query(callback_query["id"], "Не знайдено — можливо, застаріло.")
                return "ok"
            rule = feedback.add_feedback(item["link"], item["title"], item["description"])
            cache = _load_classification_cache()
            cache.pop(item["link"], None)
            _save_classification_cache(cache)
            telegram_bot.answer_callback_query(callback_query["id"], "Дякую! Враховано.")
            log.info("Feedback: rule=%r link=%s", rule, item["link"])
            return "ok"

        if data.startswith("digest:"):
            hours = float(data.split(":", 1)[1])
            sub = subscribers.get(str(chat_id))
            if not sub:
                telegram_bot.answer_callback_query(callback_query["id"])
                telegram_bot.send_message(chat_id, "Спочатку обери настрій через /start.")
                return "ok"
            log.info("chat_id=%s requested digest: mood=%s hours=%s", chat_id, sub["mood"], hours)
            entries = history.query(sub["mood"], hours)
            telegram_bot.answer_callback_query(callback_query["id"])
            telegram_bot.send_message(chat_id, _format_digest(sub["mood"], hours, entries))
            return "ok"

        if data == "mood:stop":
            subscribers.pop(str(chat_id), None)
            telegram_bot.save_subscribers(subscribers)
            telegram_bot.answer_callback_query(callback_query["id"], "Відписано")
            telegram_bot.send_message(
                chat_id, "Більше не надсилатиму новини. Напиши /start, щоб підписатися знову."
            )
            log.info("chat_id=%s unsubscribed", chat_id)
        elif data.startswith("mood:"):
            mood = data.split(":", 1)[1]
            # Preserve any existing donate-reminder timestamp (see
            # broadcaster.py) instead of wiping it out on a mood change —
            # but a brand new subscriber just saw the donate line in
            # START_TEXT, so start their reminder clock now instead of
            # having broadcaster.py nag them again within the hour.
            existing = subscribers.get(str(chat_id), {"last_donate_reminder": time.time()})
            existing["mood"] = mood
            subscribers[str(chat_id)] = existing
            telegram_bot.save_subscribers(subscribers)
            telegram_bot.answer_callback_query(callback_query["id"], "Підписано!")
            confirm = MOOD_CONFIRM_TEXT[mood] + "\n\n🔄 /mood — змінити будь-коли"
            telegram_bot.send_message(chat_id, confirm)
            log.info("chat_id=%s subscribed to mood=%s", chat_id, mood)
        return "ok"

    return "ok"


if __name__ == "__main__":
    host = os.environ.get("HOST", "0.0.0.0")
    port = int(os.environ.get("PORT", 8600))
    log.info("Starting on %s:%d", host, port)
    app.run(host=host, port=port)
