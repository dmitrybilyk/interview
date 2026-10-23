"""
District Alert Bot — Львів.
Підписка на зону → алерт коли в @lvivych_news з'явиться повідомлення про цей район.

Запуск: venv/bin/python shahed_bot/bot.py
"""

import io
import json
import logging
import re
import threading
import time
from pathlib import Path

import requests
from bs4 import BeautifulSoup
from staticmap import StaticMap, CircleMarker, Line

from zones import ZONES, ZONE_COORDS

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)-7s %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("shahed_bot")

# ── Config ────────────────────────────────────────────────────────────────────

TOKEN_FILE   = Path(__file__).parent / "token.txt"
SUBS_FILE    = Path(__file__).parent / "subscribers.json"
STATE_FILE   = Path(__file__).parent / "poll_state.json"
CHANNEL             = "lvivych_news"
POLL_SECONDS_NORMAL = 15
POLL_SECONDS_ALERT  = 3

TOKEN        = TOKEN_FILE.read_text().strip() if TOKEN_FILE.exists() else ""
API          = f"https://api.telegram.org/bot{TOKEN}"
DONATE_CARD  = "4441 1110 3446 3160"
DONATE_TEXT  = f"☕ Підтримати розробника (Monobank):\n<code>{DONATE_CARD}</code>"

# ── Telegram helpers ──────────────────────────────────────────────────────────

def tg(method: str, **kwargs) -> dict:
    try:
        r = requests.post(f"{API}/{method}", json=kwargs, timeout=10)
        return r.json()
    except Exception as e:
        log.warning("tg %s failed: %s", method, e)
        return {}


def send(chat_id: int, text: str, **kwargs) -> None:
    kwargs.setdefault("disable_web_page_preview", True)
    tg("sendMessage", chat_id=chat_id, text=text, **kwargs)


def answer(callback_id: str, text: str = "") -> None:
    tg("answerCallbackQuery", callback_query_id=callback_id, text=text)


def send_zone_map(chat_id: int, zone_key: str) -> None:
    coords = ZONE_COORDS.get(zone_key)
    if not coords:
        return
    lat, lon, radius_km = coords

    try:
        m = StaticMap(600, 600, url_template="https://tile.openstreetmap.org/{z}/{x}/{y}.png")

        # Коло зони — апроксимуємо точками по периметру
        import math
        points = []
        for deg in range(0, 361, 10):
            rad = math.radians(deg)
            dlat = (radius_km / 111) * math.cos(rad)
            dlon = (radius_km / (111 * math.cos(math.radians(lat)))) * math.sin(rad)
            points.append((lon + dlon, lat + dlat))

        line = Line(points, "#FF4444", 3)
        m.add_line(line)

        # Центр зони
        center = CircleMarker((lon, lat), "#FF4444", 10)
        m.add_marker(center)

        img = m.render(zoom=11)
        buf = io.BytesIO()
        img.save(buf, format="PNG")
        buf.seek(0)

        requests.post(
            f"{API}/sendPhoto",
            data={"chat_id": chat_id},
            files={"photo": ("map.png", buf, "image/png")},
            timeout=15,
        )
    except Exception as e:
        log.warning("map generation failed: %s", e)

# ── Subscribers ───────────────────────────────────────────────────────────────

def load_subs() -> dict:
    if SUBS_FILE.exists():
        return json.loads(SUBS_FILE.read_text())
    return {}


def save_subs(subs: dict) -> None:
    SUBS_FILE.write_text(json.dumps(subs, ensure_ascii=False, indent=2))

# ── Keyboards ─────────────────────────────────────────────────────────────────

def zone_keyboard(current_zone: str | None = None) -> dict:
    rows = []
    for key, zone in ZONES.items():
        mark = " ✅" if key == current_zone else ""
        rows.append([{"text": zone["name"] + mark, "callback_data": f"zone:{key}"}])
    rows.append([{"text": "🔕 Відписатися", "callback_data": "zone:off"}])
    rows.append([{"text": "☕ Підтримати розробника", "callback_data": "donate"}])
    return {"inline_keyboard": rows}

# ── Channel polling ───────────────────────────────────────────────────────────

def load_seen() -> set:
    if STATE_FILE.exists():
        return set(json.loads(STATE_FILE.read_text()))
    return set()


def save_seen(seen: set) -> None:
    STATE_FILE.write_text(json.dumps(list(seen)))


def fetch_messages() -> list[dict]:
    try:
        r = requests.get(
            f"https://t.me/s/{CHANNEL}",
            timeout=10,
            headers={"User-Agent": "Mozilla/5.0"},
        )
        r.raise_for_status()
    except Exception as e:
        log.warning("fetch failed: %s", e)
        return []

    soup = BeautifulSoup(r.text, "html.parser")
    msgs = []
    for el in soup.select(".tgme_widget_message"):
        msg_id  = el.get("data-post", "")
        text_el = el.select_one(".tgme_widget_message_text")
        text    = text_el.get_text(" ", strip=True) if text_el else ""
        # Прибираємо футер каналу виду "😊 Назва | Підписатися"
        text = re.sub(r'\s*.{0,30}\|\s*Підписатися', '', text).strip()
        if msg_id and text:
            msgs.append({"id": msg_id, "text": text})
    return msgs


# Надсилається ВСІМ підписникам незалежно від зони
_BROADCAST_RE = re.compile(
    r"повітряна тривог|повітряній тривоз|\bвідбій\b|дорозвідк"
    r"|\bппо\b|\bзрк\b|буде гучно|над містом|навколо львов|над львов",
    flags=re.IGNORECASE,
)


def is_broadcast(text: str) -> bool:
    return bool(_BROADCAST_RE.search(text))


def broadcast_prefix(text: str) -> str:
    t = text.lower()
    if "відбій" in t:
        return "✅"
    if "дорозвідк" in t:
        return "🔍"
    return "🚨"


def matched_zones(text: str) -> list[str]:
    text_lower = text.lower()
    matched = []
    for key, zone in ZONES.items():
        pattern = re.compile(
            "|".join(re.escape(s) for s in zone["stems"]),
            flags=re.IGNORECASE,
        )
        if pattern.search(text_lower):
            matched.append(key)
    return matched


def poll_loop() -> None:
    log.info("Поллінг @%s нормально=%ds тривога=%ds",
             CHANNEL, POLL_SECONDS_NORMAL, POLL_SECONDS_ALERT)
    seen          = load_seen()
    alert_active  = False

    if not seen:
        msgs = fetch_messages()
        seen = {m["id"] for m in msgs}
        save_seen(seen)
        log.info("Перший запуск — запам'ятовано %d постів", len(seen))

    while True:
        time.sleep(POLL_SECONDS_ALERT if alert_active else POLL_SECONDS_NORMAL)
        msgs = fetch_messages()
        new  = [m for m in msgs if m["id"] not in seen]

        for msg in new:
            seen.add(msg["id"])
            broadcast = is_broadcast(msg["text"])
            hit_zones = matched_zones(msg["text"])

            if not broadcast and not hit_zones:
                continue

            # Оновлюємо стан тривоги → змінюємо інтервал поллінгу
            if broadcast:
                t = msg["text"].lower()
                if "відбій" in t and alert_active:
                    alert_active = False
                    log.info("Відбій — поллінг %ds", POLL_SECONDS_NORMAL)
                elif "відбій" not in t and not alert_active:
                    alert_active = True
                    log.info("Тривога — поллінг %ds", POLL_SECONDS_ALERT)

            log.info("broadcast=%s zones=%s | %s", broadcast, hit_zones, msg["text"][:80])
            subs = load_subs()

            for chat_id, sub in subs.items():
                zone = sub.get("zone")
                if not zone:
                    continue

                if broadcast:
                    prefix = broadcast_prefix(msg["text"])
                    alert  = f"{prefix} {msg['text']}"
                    send(int(chat_id), alert)
                elif "all" in hit_zones or zone == "all" or zone in hit_zones:
                    # "all" в hit_zones → загальноміська новина → всі підписники
                    zone_name = ZONES.get(zone, {}).get("name", zone)
                    alert = f"⚠️ ШАХЕДИ — {zone_name}\n\n{msg['text']}"
                    send(int(chat_id), alert)

        if new:
            save_seen(seen)


# ── Bot updates (long polling) ────────────────────────────────────────────────

def handle_update(update: dict) -> None:
    if msg := update.get("message"):
        chat_id = msg["chat"]["id"]
        text    = msg.get("text", "")

        if text.startswith("/start") or text.startswith("/zone"):
            subs     = load_subs()
            sub      = subs.get(str(chat_id), {})
            current  = sub.get("zone")
            greeting = (
                "🚨 Сповіщення по районах — Львів\n\n"
                "Обери свій район — надішлю сповіщення щойно з'явиться "
                "повідомлення про твою зону.\n\n"
                "ℹ️ Як це працює:\n"
                "• Бот моніторить публічні джерела\n"
                "• Фільтрує повідомлення за назвами населених пунктів і районів\n"
                "• Надсилає тобі лише ті, що стосуються твоєї зони\n\n"
                "⚡️ Корисно коли відстежуються шахеди або ракети і важливо "
                "знати чи рухаються вони в бік твого району.\n\n"
                "⚠️ Це не офіційне джерело. Завжди орієнтуйся на сигнал "
                "повітряної тривоги та офіційні канали ДСНС."
            )
            send(chat_id, greeting, reply_markup=zone_keyboard(current))

    if cq := update.get("callback_query"):
        chat_id = cq["message"]["chat"]["id"]
        data    = cq.get("data", "")

        if data == "donate":
            answer(cq["id"])
            send(chat_id, DONATE_TEXT,
                 parse_mode="HTML",
                 reply_markup={"inline_keyboard": [[{"text": "📋 Скопіювати", "callback_data": "copy_card"}]]})

        elif data == "copy_card":
            answer(cq["id"], "✅ Скопійовано!")

        elif data.startswith("zone:"):
            zone = data.split(":", 1)[1]
            subs = load_subs()

            if zone == "off":
                subs.pop(str(chat_id), None)
                save_subs(subs)
                answer(cq["id"], "Відписано")
                send(chat_id, "🔕 Відписався. /start — підписатися знову.")
            else:
                subs[str(chat_id)] = {"zone": zone}
                save_subs(subs)
                zone_name = ZONES[zone]["name"]
                answer(cq["id"], "✅ Збережено!")
                send(chat_id,
                    f"✅ Зону збережено: {zone_name}\n"
                    "Надішлю алерт як тільки твій район з'явиться в новинах.\n\n"
                    "/zone — змінити зону.",
                    reply_markup=zone_keyboard(zone),
                )
                send_zone_map(chat_id, zone)
            log.info("chat_id=%s zone=%s", chat_id, zone)


def updates_loop() -> None:
    offset = 0
    log.info("Слухаю оновлення бота...")
    while True:
        try:
            r = requests.post(f"{API}/getUpdates", json={"offset": offset, "timeout": 30}, timeout=35)
            result = r.json()
            for upd in result.get("result", []):
                offset = upd["update_id"] + 1
                handle_update(upd)
        except Exception as e:
            log.warning("getUpdates error: %s", e)
            time.sleep(5)


# ── Main ──────────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    if not TOKEN:
        print("Заповни shahed_bot/token.txt токеном від @BotFather")
    else:
        threading.Thread(target=poll_loop, daemon=True).start()
        updates_loop()
