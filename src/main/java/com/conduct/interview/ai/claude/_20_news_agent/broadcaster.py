"""Запускається кожні 15 хв (systemd timer). Пушить нові новини підписникам у Telegram.

sent_state.json — які посилання вже надіслані per mood (щоб не слати двічі).
Перший запуск для mood тільки записує baseline — нові підписники не отримують бекло.
"""

import json
import logging
import time
from pathlib import Path
import telegram_bot
from telegram_bot import BotBlockedError, is_configured, load_subscribers, save_subscribers, send_message
from agent import CATEGORY_LABELS, get_filtered_news, get_sports_news, get_tech_news, history

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
    blocked_ids: set[str] = set()

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
                keyboard = {"inline_keyboard": [
                    [{"text": "📊 Дайджест за 24 години", "callback_data": "digest:24"}],
                    [{"text": "📊 Дайджест за 7 днів",    "callback_data": "digest:168"}],
                ]}
                for chat_id in chat_ids:
                    if chat_id in blocked_ids:
                        continue
                    try:
                        send_message(chat_id, text, reply_markup=keyboard)
                    except BotBlockedError:
                        blocked_ids.add(chat_id)
        elif is_first_run_for_mood:
            log.info("mood=%s: first run — seeding baseline of %d item(s), nothing sent", mood, len(items))
        else:
            log.info("mood=%s: nothing new since last run", mood)

        all_links = list(already_seen | {it["link"] for it in items})
        state[mood] = all_links[-MAX_TRACKED_LINKS_PER_MOOD:]

    # ── Спортивні новини ────────────────────────────────────────────────────────
    time.sleep(5)  # пауза після основних новин щоб не вичерпати Groq rate limit
    sports_subs = [cid for cid, sub in subscribers.items() if sub.get("sports")]
    if sports_subs:
        sports_items = get_sports_news()
        already_seen_sports = set(state.get("sports", []))
        is_first_sports = not already_seen_sports
        new_sports = [it for it in sports_items if it["link"] not in already_seen_sports]

        if not is_first_sports and new_sports:
            log.info("sports: pushing %d new item(s) to %d subscriber(s)", len(new_sports), len(sports_subs))
            sports_keyboard = {"inline_keyboard": [
                [{"text": "📊 Дайджест за 24 години", "callback_data": "digest:24"}],
                [{"text": "📊 Дайджест за 7 днів",    "callback_data": "digest:168"}],
            ]}
            for item in new_sports:
                text = f"🏆 Спорт\n<b>{item['title']}</b>\n{item['description']}\n{item['link']}"
                for chat_id in sports_subs:
                    if chat_id in blocked_ids:
                        continue
                    try:
                        send_message(chat_id, text, reply_markup=sports_keyboard)
                    except BotBlockedError:
                        blocked_ids.add(chat_id)
        elif is_first_sports:
            log.info("sports: first run — seeding baseline of %d item(s)", len(sports_items))
        else:
            log.info("sports: nothing new since last run")

        all_sports_links = list(already_seen_sports | {it["link"] for it in sports_items})
        state["sports"] = all_sports_links[-MAX_TRACKED_LINKS_PER_MOOD:]

    # ── IT-новини ────────────────────────────────────────────────────────────────
    tech_subs = [cid for cid, sub in subscribers.items() if sub.get("tech")]
    if tech_subs:
        time.sleep(5)
        tech_items = get_tech_news()
        already_seen_tech = set(state.get("tech", []))
        is_first_tech = not already_seen_tech
        new_tech = [it for it in tech_items if it["link"] not in already_seen_tech]

        if not is_first_tech and new_tech:
            log.info("tech: pushing %d new item(s) to %d subscriber(s)", len(new_tech), len(tech_subs))
            tech_keyboard = {"inline_keyboard": [
                [{"text": "📊 Дайджест за 24 години", "callback_data": "digest:24"}],
                [{"text": "📊 Дайджест за 7 днів",    "callback_data": "digest:168"}],
            ]}
            for item in new_tech:
                text = f"💻 IT\n<b>{item['title']}</b>\n{item['description']}\n{item['link']}"
                for chat_id in tech_subs:
                    if chat_id in blocked_ids:
                        continue
                    try:
                        send_message(chat_id, text, reply_markup=tech_keyboard)
                    except BotBlockedError:
                        blocked_ids.add(chat_id)
        elif is_first_tech:
            log.info("tech: first run — seeding baseline of %d item(s)", len(tech_items))
        else:
            log.info("tech: nothing new since last run")

        all_tech_links = list(already_seen_tech | {it["link"] for it in tech_items})
        state["tech"] = all_tech_links[-MAX_TRACKED_LINKS_PER_MOOD:]

    _save_state(state)

    if blocked_ids:
        for cid in blocked_ids:
            subscribers.pop(cid, None)
        save_subscribers(subscribers)
        log.info("Auto-unsubscribed %d blocked user(s): %s", len(blocked_ids), blocked_ids)

    _send_due_donate_reminders(subscribers)


def _send_due_donate_reminders(subscribers: dict) -> None:
    """Надсилає нагадування про донат раз на місяць кожному підписнику."""
    now = time.time()
    changed = False

    for chat_id, sub in subscribers.items():
        last = sub.get("last_donate_reminder", 0)
        if now - last < telegram_bot.DONATE_REMINDER_INTERVAL_SECONDS:
            continue
        try:
            send_message(chat_id, telegram_bot.DONATE_LINE, reply_markup=telegram_bot.DONATE_KEYBOARD)
        except BotBlockedError:
            subscribers.pop(chat_id, None)
            changed = True
            log.info("Donate reminder: auto-unsubscribed blocked chat_id=%s", chat_id)
            continue
        sub["last_donate_reminder"] = now
        changed = True
        log.info("Нагадування про донат надіслано chat_id=%s", chat_id)

    if changed:
        save_subscribers(subscribers)


if __name__ == "__main__":
    main()
