"""Flask: веб-сторінка (?mood=...) і Telegram webhook.

Запуск локально:
    venv/bin/python web.py   → http://localhost:8600
"""

import logging
import os

from flask import Flask, render_template, request

import telegram_bot
import tg
from agent import get_filtered_news

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)-7s [%(name)s] %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("news_agent.web")

app   = Flask(__name__)
MOODS = ("positive", "mostly_positive", "all")


@app.route("/", methods=["GET"])
def index():
    mood  = request.args.get("mood")
    news  = None
    error = None
    if mood in MOODS:
        log.info("web request mood=%s", mood)
        try:
            news = get_filtered_news(mood)
        except Exception as exc:  # noqa: BLE001
            log.exception("get_filtered_news failed mood=%s", mood)
            error = str(exc)
    bot_username = telegram_bot.get_bot_username() if telegram_bot.is_configured() else None
    return render_template("index.html", mood=mood, news=news, error=error, bot_username=bot_username)


@app.route("/telegram-webhook", methods=["POST"])
def telegram_webhook():
    update = request.get_json(silent=True) or {}
    return tg.handle(update)


if __name__ == "__main__":
    host = os.environ.get("HOST", "0.0.0.0")
    port = int(os.environ.get("PORT", 8600))
    log.info("Starting on %s:%d", host, port)
    app.run(host=host, port=port)
