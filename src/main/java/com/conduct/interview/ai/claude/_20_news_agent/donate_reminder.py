"""
Прогресивне нагадування про донат:
  - 1-й раз: через тиждень
  - 2-й раз: через 2 тижні
  - далі: раз на місяць постійно

Запускається systemd timer щодня — сам вирішує чи час надсилати.
"""

import json
import logging
import time
from pathlib import Path

import requests

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(message)s")
log = logging.getLogger("donate_reminder")

HERE      = Path(__file__).parent
STATE_FILE = HERE / "donate_state.json"

DONATE_CARD = "4441 1110 3446 3160"
TEXT = (
    "☕ На каву розробнику 😊\n\n"
    f"Monobank: <code>{DONATE_CARD}</code>"
)
KEYBOARD = {"inline_keyboard": [[{"text": "📋 Скопіювати номер", "callback_data": "copy_card"}]]}

# Інтервали в секундах
INTERVALS = [
    7  * 24 * 3600,   # після 1-го надсилання — чекати тиждень
    14 * 24 * 3600,   # після 2-го — 2 тижні
    30 * 24 * 3600,   # далі — місяць
]

BOTS = [
    {
        "token_file": HERE / "telegram_token.txt",
        "subs_file":  HERE / "subscribers.json",
        "get_ids":    lambda subs: [cid for cid, s in subs.items() if s.get("mood")],
    },
    {
        "token_file": HERE / "shahed_bot" / "token.txt",
        "subs_file":  HERE / "shahed_bot" / "subscribers.json",
        "get_ids":    lambda subs: list(subs.keys()),
    },
    {
        "token_file": HERE / "kyiv_bot" / "token.txt",
        "subs_file":  HERE / "kyiv_bot" / "subscribers.json",
        "get_ids":    lambda subs: list(subs.keys()),
    },
]


def load_state() -> dict:
    if STATE_FILE.exists():
        return json.loads(STATE_FILE.read_text())
    return {"count": 0, "next_send": 0}


def save_state(state: dict) -> None:
    STATE_FILE.write_text(json.dumps(state))


def send(token: str, chat_id: int) -> None:
    try:
        requests.post(
            f"https://api.telegram.org/bot{token}/sendMessage",
            json={"chat_id": chat_id, "text": TEXT,
                  "parse_mode": "HTML", "reply_markup": KEYBOARD},
            timeout=10,
        )
    except Exception as e:
        log.warning("send failed chat_id=%s: %s", chat_id, e)


def main() -> None:
    state = load_state()
    now   = time.time()

    if now < state["next_send"]:
        log.info("Ще не час. Наступне надсилання: %s",
                 time.strftime("%Y-%m-%d %H:%M", time.localtime(state["next_send"])))
        return

    total = 0
    for bot in BOTS:
        if not bot["token_file"].exists() or not bot["subs_file"].exists():
            continue
        token = bot["token_file"].read_text().strip()
        subs  = json.loads(bot["subs_file"].read_text())
        ids   = bot["get_ids"](subs)
        log.info("%s → %d підписників", bot["token_file"].parent.name, len(ids))
        for chat_id in ids:
            send(token, int(chat_id))
        total += len(ids)

    count    = state["count"] + 1
    interval = INTERVALS[min(count, len(INTERVALS) - 1)]
    state    = {"count": count, "next_send": now + interval}
    save_state(state)
    log.info("Надіслано %d юзерам. Наступне через %d днів (%s)",
             total, interval // 86400,
             time.strftime("%Y-%m-%d", time.localtime(state["next_send"])))
