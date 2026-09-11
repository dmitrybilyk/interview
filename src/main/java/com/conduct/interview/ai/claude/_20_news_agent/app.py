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

from flask import Flask, render_template, request

import telegram_bot
from agent import get_filtered_news

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)-7s [%(name)s] %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("news_agent.app")

app = Flask(__name__)

MOODS = ("positive", "negative", "all")

MOOD_KEYBOARD = {
    "inline_keyboard": [
        [{"text": "🙂 Позитивні", "callback_data": "mood:positive"}],
        [{"text": "😟 Негативні", "callback_data": "mood:negative"}],
        [{"text": "📰 Усі новини", "callback_data": "mood:all"}],
        [{"text": "🛑 Відписатися", "callback_data": "mood:stop"}],
    ]
}

# "Positive"/"negative" are from a Ukrainian reader's point of view, not a
# generic mood — spell that out so it's never ambiguous what someone is
# subscribing to (see agent/moods.py for the exact rules behind each).
START_TEXT = (
    "Привіт! Обери, які новини України надсилати:\n\n"
    "🙂 Позитивні — гарні новини ДЛЯ УКРАЇНИ (тобто погані для Росії: втрати "
    "окупантів, удари по території РФ, проблеми в економіці РФ). Відбираються AI.\n"
    "😟 Негативні — важкі новини для України. Теж відбираються AI.\n"
    "📰 Усі — без фільтрації, взагалі без участі AI"
)

MOOD_CONFIRM_TEXT = {
    "positive": "Готово! Надсилатиму українські позитивні новини (погані для Росії), відібрані для тебе AI, щойно з'являться нові.",
    "negative": "Готово! Надсилатиму українські негативні новини, відібрані для тебе AI, щойно з'являться нові.",
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
    if message and message.get("text", "").startswith("/start"):
        chat_id = message["chat"]["id"]
        log.info("Telegram /start from chat_id=%s", chat_id)
        telegram_bot.send_message(chat_id, START_TEXT, reply_markup=MOOD_KEYBOARD)
        return "ok"

    if message and message.get("text", "").startswith("/donate"):
        chat_id = message["chat"]["id"]
        telegram_bot.send_message(chat_id, telegram_bot.DONATE_LINE)
        return "ok"

    callback_query = update.get("callback_query")
    if callback_query:
        chat_id = callback_query["message"]["chat"]["id"]
        data = callback_query.get("data", "")
        log.info("Telegram button press from chat_id=%s: %s", chat_id, data)
        subscribers = telegram_bot.load_subscribers()

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
            # START_TEXT, so start their weekly clock now instead of
            # having broadcaster.py nag them again within the hour.
            existing = subscribers.get(str(chat_id), {"last_donate_reminder": time.time()})
            existing["mood"] = mood
            subscribers[str(chat_id)] = existing
            telegram_bot.save_subscribers(subscribers)
            telegram_bot.answer_callback_query(callback_query["id"], "Підписано!")
            telegram_bot.send_message(chat_id, MOOD_CONFIRM_TEXT[mood])
            log.info("chat_id=%s subscribed to mood=%s", chat_id, mood)
        return "ok"

    return "ok"


if __name__ == "__main__":
    host = os.environ.get("HOST", "0.0.0.0")
    port = int(os.environ.get("PORT", 8600))
    log.info("Starting on %s:%d", host, port)
    app.run(host=host, port=port)
