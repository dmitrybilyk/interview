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

TOKEN_FILE         = Path(__file__).parent / "token.txt"
GROQ_KEY_FILE      = Path(__file__).parent / "groq_key.txt"
SUBS_FILE          = Path(__file__).parent / "subscribers.json"
STATE_FILE         = Path(__file__).parent / "poll_state.json"
DONATE_TS_FILE     = Path(__file__).parent / "donate_ts.json"
POSITIVE_SENT_FILE = Path(__file__).parent / "positive_sent.json"
HISTORY_FILE       = Path(__file__).parent.parent / "history.json"  # news-agent shared file
CHANNEL              = "lvivych_news"
POLL_SECONDS_NORMAL  = 15
POLL_SECONDS_ALERT   = 3
DONATE_COOLDOWN      = 3 * 24 * 3600   # 3 days
POSITIVE_INTERVAL    = 10 * 60         # check positive news every 10 min
POSITIVE_MAX_AGE     = 24 * 3600       # only news from last 24h

TOKEN        = TOKEN_FILE.read_text().strip() if TOKEN_FILE.exists() else ""
GROQ_KEY     = GROQ_KEY_FILE.read_text().strip() if GROQ_KEY_FILE.exists() else ""
API          = f"https://api.telegram.org/bot{TOKEN}"
DONATE_CARD     = "4441 1110 3446 3160"
DONATE_TEXT     = f"☕ Підтримати розробника (Monobank):\n<code>{DONATE_CARD}</code>"
DONATE_REMINDER = (
    "☕ Відбій. Якщо бот був корисний — можна підтримати розробника:\n"
    f"<code>{DONATE_CARD}</code> (Monobank)"
)

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

        import math
        points = []
        for deg in range(0, 361, 10):
            rad = math.radians(deg)
            dlat = (radius_km / 111) * math.cos(rad)
            dlon = (radius_km / (111 * math.cos(math.radians(lat)))) * math.sin(rad)
            points.append((lon + dlon, lat + dlat))

        line = Line(points, "#FF4444", 3)
        m.add_line(line)

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

# ── Groq AI filter ────────────────────────────────────────────────────────────

# Keyword fallback when Groq key is not configured
_THREAT_RE = re.compile(
    r"шахед|дрон|\bбпла\b|ракет|авіац|вибух|\bппо\b|\bзрк\b|обстріл|балістик"
    r"|відключ.{0,15}світл|знеструм|відключ.{0,15}електр"
    r"|відключ.{0,15}вод|без.{0,10}води|водопостач|гарячої вод|холодної вод"
    r"|відключ.{0,15}газ|газопостач|без.{0,10}газ"
    r"|\bопален\b|відключ.{0,15}тепл"
    r"|перекрит.{0,20}дорог|\bзатор\b|\bдтп\b",
    flags=re.IGNORECASE,
)


def groq_is_relevant(text: str) -> bool:
    """Ask Groq if this message is relevant to send.
    Relevant = air threat (drone/rocket/air defense) OR power outage in Lviv district OR major Lviv traffic jam.
    Falls back to keyword check if Groq key is not configured or unavailable.
    """
    if not GROQ_KEY:
        return bool(_THREAT_RE.search(text))

    prompt = (
        "Повідомлення з новинного каналу Львова. "
        "Відповідай ТАК лише якщо повідомлення стосується ОДНОГО з: "
        "1) загроза з повітря — шахед, дрон, ракета, авіація, вибух, ппо, зрк, обстріл; "
        "2) відключення електрики у конкретному районі (де саме немає світла); "
        "3) відключення або аварія водопостачання у конкретному районі; "
        "4) відключення газу або опалення у конкретному районі. "
        "Якщо це новини, протести, кримінал, суд, культура, спорт, реклама — НІ. "
        "Відповідь одним словом: ТАК або НІ.\n\n"
        f"Повідомлення: {text[:400]}"
    )

    for attempt in range(3):
        try:
            r = requests.post(
                "https://api.groq.com/openai/v1/chat/completions",
                headers={"Authorization": f"Bearer {GROQ_KEY}", "Content-Type": "application/json"},
                json={
                    "model": "llama-3.1-8b-instant",
                    "messages": [{"role": "user", "content": prompt}],
                    "max_tokens": 5,
                    "temperature": 0,
                },
                timeout=10,
            )
            if r.status_code == 429:
                retry_after = float(r.headers.get("retry-after", "1"))
                log.warning("Groq rate limit, retry in %.1fs (attempt %d)", retry_after, attempt + 1)
                time.sleep(min(retry_after, 3))
                continue
            r.raise_for_status()
            reply = r.json()["choices"][0]["message"]["content"].strip().upper()
            result = "ТАК" in reply or "YES" in reply
            log.debug("Groq: %s → %s", text[:60], result)
            return result
        except Exception as e:
            log.warning("Groq failed (attempt %d): %s", attempt + 1, e)
            if attempt < 2:
                time.sleep(1)

    log.warning("Groq unavailable — falling back to keyword filter")
    return bool(_THREAT_RE.search(text))

# ── Subscribers ───────────────────────────────────────────────────────────────

def load_subs() -> dict:
    if SUBS_FILE.exists():
        return json.loads(SUBS_FILE.read_text())
    return {}


def save_subs(subs: dict) -> None:
    SUBS_FILE.write_text(json.dumps(subs, ensure_ascii=False, indent=2))


def load_donate_ts() -> float:
    if DONATE_TS_FILE.exists():
        return json.loads(DONATE_TS_FILE.read_text()).get("last", 0.0)
    return 0.0


def save_donate_ts() -> None:
    DONATE_TS_FILE.write_text(json.dumps({"last": time.time()}))


def load_positive_sent() -> set:
    if POSITIVE_SENT_FILE.exists():
        return set(json.loads(POSITIVE_SENT_FILE.read_text()))
    return set()


def save_positive_sent(sent: set) -> None:
    links = list(sent)[-2000:]  # keep last 2000 to avoid unbounded growth
    POSITIVE_SENT_FILE.write_text(json.dumps(links))

# ── Keyboards ─────────────────────────────────────────────────────────────────

def zone_keyboard(current_zone: str | None = None, positive: bool = False) -> dict:
    rows = []
    for key, zone in ZONES.items():
        mark = " ✅" if key == current_zone else ""
        rows.append([{"text": zone["name"] + mark, "callback_data": f"zone:{key}"}])
    rows.append([{"text": "🔕 Відписатися", "callback_data": "zone:off"}])
    pos_text = "💥 Втрати ворога і удари по рф ✅" if positive else "💥 Показувати втрати ворога і удари по рф"
    rows.append([{"text": pos_text, "callback_data": "positive:toggle"}])
    rows.append([{"text": "☕ Підтримати розробника", "callback_data": "donate"}])
    return {"inline_keyboard": rows}

# ── Channel polling ───────────────────────────────────────────────────────────

def load_seen() -> set:
    if STATE_FILE.exists():
        return set(json.loads(STATE_FILE.read_text()))
    return set()


def save_seen(seen: set) -> None:
    STATE_FILE.write_text(json.dumps(list(seen)))


def send_positive_news() -> None:
    if not HISTORY_FILE.exists():
        return
    try:
        history = json.loads(HISTORY_FILE.read_text())
    except Exception as e:
        log.warning("history.json read error: %s", e)
        return

    cutoff = time.time() - POSITIVE_MAX_AGE
    fresh = [
        e for e in history.values()
        if e.get("moods", {}).get("positive")
        and e.get("first_seen", 0) >= cutoff
    ]
    if not fresh:
        return

    sent = load_positive_sent()
    new_items = [e for e in fresh if e["link"] not in sent]
    if not new_items:
        return

    subs = load_subs()
    positive_subs = [cid for cid, sub in subs.items() if sub.get("positive") and sub.get("zone")]
    if not positive_subs:
        for item in new_items:
            sent.add(item["link"])
        save_positive_sent(sent)
        return

    new_items.sort(key=lambda e: e.get("first_seen", 0))
    for item in new_items:
        title = item.get("title", "").strip()
        desc  = item.get("description", "").strip()
        link  = item.get("link", "")
        text  = f"💥 Втрати і удари по рф\n\n{title}"
        if desc and desc != title:
            text += f"\n{desc}"
        if link:
            text += f"\n{link}"
        for chat_id in positive_subs:
            send(int(chat_id), text, disable_web_page_preview=False)
        sent.add(item["link"])

    save_positive_sent(sent)
    log.info("Positive news: sent %d item(s) to %d subs", len(new_items), len(positive_subs))


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
        if not text_el:
            continue
        for a in text_el.find_all("a"):
            if "Підписатися" in a.get_text():
                a.decompose()
        text = text_el.get_text(" ", strip=True)
        text = re.sub(r'\s*\S{1,30}\s*\|\s*$', '', text)
        text = re.sub(r'\s{2,}', ' ', text).strip()
        if msg_id and text:
            msgs.append({"id": msg_id, "text": text})
    return msgs


# Fast-path reject: obvious commercial/entertainment content — skip before zone check
_PROMO_RE = re.compile(
    r"квитк[иіо]|internet-bilet|concert\.lviv|квиток\b|афіш[аи]",
    flags=re.IGNORECASE,
)


def is_promo(text: str) -> bool:
    return bool(_PROMO_RE.search(text))


# Fast-path broadcast: air alerts, all-clear, air defense activity — bypass Groq, send to all
_BROADCAST_RE = re.compile(
    r"повітряна тривог|повітряній тривоз|\bвідбій\b|дорозвідк"
    r"|\bппо\b|\bзрк\b|буде гучно|над містом|навколо львов|над львов"
    r"|балістик|загроза.*з (курськ|білорус|крим)|крилат.*ракет",
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
    log.info("Поллінг @%s нормально=%ds тривога=%ds groq=%s",
             CHANNEL, POLL_SECONDS_NORMAL, POLL_SECONDS_ALERT, "✅" if GROQ_KEY else "❌")
    seen               = load_seen()
    alert_active       = False
    last_positive_check = 0.0

    if not seen:
        msgs = fetch_messages()
        seen = {m["id"] for m in msgs}
        save_seen(seen)
        log.info("Перший запуск — запам'ятовано %d постів", len(seen))

    while True:
        time.sleep(POLL_SECONDS_ALERT if alert_active else POLL_SECONDS_NORMAL)

        now = time.time()
        if now - last_positive_check >= POSITIVE_INTERVAL:
            last_positive_check = now
            try:
                send_positive_news()
            except Exception as e:
                log.warning("positive news check failed: %s", e)
        msgs = fetch_messages()
        new  = [m for m in msgs if m["id"] not in seen]

        for msg in new:
            seen.add(msg["id"])
            broadcast = is_broadcast(msg["text"])
            hit_zones = matched_zones(msg["text"])

            if not broadcast and not hit_zones:
                continue

            if not broadcast and is_promo(msg["text"]):
                log.info("Promo filtered: %s", msg["text"][:80])
                continue

            # For zone-specific messages: verify with Groq before sending
            if not broadcast and hit_zones:
                if not groq_is_relevant(msg["text"]):
                    log.info("Groq filtered out: %s", msg["text"][:80])
                    continue

            # Update alert state → change polling interval
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
                    send(int(chat_id), f"{prefix} {msg['text']}")
                elif "all" in hit_zones or zone == "all" or zone in hit_zones:
                    zone_name = ZONES.get(zone, {}).get("name", zone)
                    send(int(chat_id), f"⚠️ {zone_name}\n\n{msg['text']}")

            # Після відбою — ненав'язливе нагадування про донат (не частіше ніж раз на 3 дні)
            if broadcast and broadcast_prefix(msg["text"]) == "✅":
                if time.time() - load_donate_ts() >= DONATE_COOLDOWN:
                    for chat_id, sub in subs.items():
                        if sub.get("zone"):
                            send(int(chat_id), DONATE_REMINDER, parse_mode="HTML")
                    save_donate_ts()
                    log.info("Donation reminder sent after відбій")

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
            positive = sub.get("positive", False)
            greeting = (
                "🚨 Сповіщення по районах — Львів\n\n"
                "Обери свій район — надішлю сповіщення щойно з'явиться "
                "повідомлення про твою зону.\n\n"
                "ℹ️ Як це працює:\n"
                "• Бот моніторить публічні джерела\n"
                "• Фільтрує повідомлення за назвами населених пунктів і районів\n"
                "• Використовує ШІ для відсіву нерелевантних новин\n"
                "• Надсилає тобі лише ті, що стосуються твоєї зони\n\n"
                "⚡️ Сповіщення про загрози з повітря, відключення води, "
                "електрики, газу та інші важливі події в районі.\n\n"
                "⚠️ Це не офіційне джерело. Завжди орієнтуйся на сигнал "
                "повітряної тривоги та офіційні канали ДСНС."
            )
            send(chat_id, greeting, reply_markup=zone_keyboard(current, positive))

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
                sub = subs.get(str(chat_id), {})
                sub["zone"] = zone
                subs[str(chat_id)] = sub
                save_subs(subs)
                zone_name = ZONES[zone]["name"]
                answer(cq["id"], "✅ Збережено!")
                send(chat_id,
                    f"✅ Зону збережено: {zone_name}\n"
                    "Надішлю алерт як тільки твій район з'явиться в новинах.\n\n"
                    "/zone — змінити зону.",
                    reply_markup=zone_keyboard(zone, sub.get("positive", False)),
                )
                send_zone_map(chat_id, zone)
            log.info("chat_id=%s zone=%s", chat_id, zone)

        elif data == "positive:toggle":
            subs = load_subs()
            sub  = subs.get(str(chat_id), {})
            if not sub.get("zone"):
                answer(cq["id"], "Спочатку обери зону")
            else:
                new_val = not sub.get("positive", False)
                sub["positive"] = new_val
                subs[str(chat_id)] = sub
                save_subs(subs)
                if new_val:
                    answer(cq["id"], "✅ Увімкнено!")
                    send(chat_id,
                        "💥 Увімкнено: Втрати ворога і удари по рф\n\n"
                        "Нові повідомлення з'являтимуться протягом кількох хвилин після публікації.",
                        reply_markup=zone_keyboard(sub["zone"], True))
                else:
                    answer(cq["id"], "Вимкнено")
                    send(chat_id, "Вимкнено: Втрати і удари по рф",
                         reply_markup=zone_keyboard(sub["zone"], False))
            log.info("chat_id=%s positive=%s", chat_id, sub.get("positive"))


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
