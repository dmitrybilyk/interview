"""Flask: веб-сторінка (?mood=...) і Telegram webhook.

Запуск локально:
    venv/bin/python web.py   → http://localhost:8600
"""

import logging
import os

from flask import Flask, render_template, request

import telegram_bot
import tg
import fb_agent
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
    subscribers = telegram_bot.load_subscribers()
    sub_counts = {"positive": 0, "mostly_positive": 0, "all": 0}
    for sub in subscribers.values():
        m = sub.get("mood")
        if m in sub_counts:
            sub_counts[m] += 1
    return render_template("index.html", mood=mood, news=news, error=error,
                           bot_username=bot_username, sub_counts=sub_counts,
                           total_subs=len(subscribers))


@app.route("/telegram-webhook", methods=["POST"])
def telegram_webhook():
    update = request.get_json(silent=True) or {}
    return tg.handle(update)


@app.route("/facebook-webhook", methods=["GET"])
def facebook_webhook_verify():
    mode      = request.args.get("hub.mode", "")
    token     = request.args.get("hub.verify_token", "")
    challenge = request.args.get("hub.challenge", "")
    result = fb_agent.verify_webhook(mode, token, challenge)
    if result:
        return result, 200
    return "Forbidden", 403


@app.route("/facebook-webhook", methods=["POST"])
def facebook_webhook_event():
    data = request.get_json(silent=True) or {}
    fb_agent.handle_webhook(data)
    return "ok", 200


if __name__ == "__main__":
    host = os.environ.get("HOST", "0.0.0.0")
    port = int(os.environ.get("PORT", 8600))
    log.info("Starting on %s:%d", host, port)
    app.run(host=host, port=port)
