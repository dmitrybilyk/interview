"""Запускається кожні 15 хв (systemd timer). Пушить нові новини підписникам у Telegram.

sent_state.json — які посилання вже надіслані per mood (щоб не слати двічі).
Перший запуск для mood тільки записує baseline — нові підписники не отримують бекло.
"""

import json
import logging
import time
from pathlib import Path

import telegram_bot
from telegram_bot import is_configured, load_subscribers, save_subscribers, send_message
from agent import CATEGORY_LABELS, feedback, get_filtered_news, history

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)-7s [%(name)s] %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("news_agent.broadcaster")

STATE_FILE = Path(__file__).resolve().parent / "sent_state.json"
MAX_TRACKED_LINKS_PER_MOOD = 300


def _load_state() -> dict:
    if STATE_FILE.exists():
        return json.loads(STATE_FILE.read_text())
    return {}


def _save_state(state: dict) -> None:
    STATE_FILE.write_text(json.dumps(state, ensure_ascii=False, indent=2))


def main():
    if not is_configured():
        log.info("No Telegram bot token configured — nothing to do.")
        return

    subscribers = load_subscribers()
    if not subscribers:
        log.info("No subscribers yet — nothing to do.")
        return

    moods_needed = {sub["mood"] for sub in subscribers.values()}
    log.info("%d subscriber(s) across mood(s): %s", len(subscribers), sorted(moods_needed))

    state = _load_state()

    for mood in moods_needed:
        items = get_filtered_news(mood)
        history.record(mood, items)  # for /digest — independent of who's due a push below

        already_seen = set(state.get(mood, []))
        is_first_run_for_mood = not already_seen
        new_items = [it for it in items if it["link"] not in already_seen]

        if not is_first_run_for_mood and new_items:
            chat_ids = [cid for cid, sub in subscribers.items() if sub["mood"] == mood]
            log.info("mood=%s: pushing %d new item(s) to %d subscriber(s)", mood, len(new_items), len(chat_ids))
            for item in new_items:
                label = CATEGORY_LABELS.get(item.get("category"))
                prefix = f"{label}\n" if label else ""
                text = f"{prefix}<b>{item['title']}</b>\n{item['description']}\n{item['link']}"
                keyboard = {"inline_keyboard": [[{"text": "📊 Дайджест", "callback_data": "digest:24"}]]}
                for chat_id in chat_ids:
                    send_message(chat_id, text, reply_markup=keyboard)
        elif is_first_run_for_mood:
            log.info("mood=%s: first run — seeding baseline of %d item(s), nothing sent", mood, len(items))
        else:
            log.info("mood=%s: nothing new since last run", mood)

        all_links = list(already_seen | {it["link"] for it in items})
        state[mood] = all_links[-MAX_TRACKED_LINKS_PER_MOOD:]

    _save_state(state)
    _send_due_donate_reminders(subscribers)


def _send_due_donate_reminders(subscribers: dict) -> None:
    """Надсилає нагадування про донат раз на місяць кожному підписнику."""
    now = time.time()
    changed = False

    for chat_id, sub in subscribers.items():
        last = sub.get("last_donate_reminder", 0)
        if now - last < telegram_bot.DONATE_REMINDER_INTERVAL_SECONDS:
            continue
        send_message(chat_id, telegram_bot.DONATE_LINE)
        sub["last_donate_reminder"] = now
        changed = True
        log.info("Sent donate reminder to chat_id=%s", chat_id)

    if changed:
        save_subscribers(subscribers)


if __name__ == "__main__":
    main()
