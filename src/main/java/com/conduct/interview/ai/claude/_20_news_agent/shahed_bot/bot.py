"""
District Alert Bot — Львів.
Онбординг → вибір зони, позитивних новин, графіку відключень.
Запуск: venv/bin/python shahed_bot/bot.py
"""

import base64
import io
import json
import logging
import math
import re
import threading
import time
from datetime import datetime, timedelta
from pathlib import Path
from zoneinfo import ZoneInfo

import anthropic
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

_DIR               = Path(__file__).parent
TOKEN_FILE         = _DIR / "token.txt"
GROQ_KEY_FILE      = _DIR / "groq_key.txt"
_KEY_CANDIDATES    = [_DIR / "key.txt", _DIR.parent / "key.txt", _DIR.parent.parent / "key.txt"]
CLAUDE_KEY_FILE    = next((p for p in _KEY_CANDIDATES if p.exists()), _KEY_CANDIDATES[1])
SUBS_FILE          = _DIR / "subscribers.json"
STATE_FILE         = _DIR / "poll_state.json"
POSITIVE_SENT_FILE = _DIR / "positive_sent.json"
OBLEN_STATE_FILE   = _DIR / "oblenergo_state.json"
OBLEN_WARNED_FILE  = _DIR / "oblenergo_warned.json"
ALERTS_LOG_FILE      = _DIR / "alerts_log.json"
WEATHER_NOTIFIED_FILE = _DIR / "weather_notified.json"
HISTORY_FILE         = _DIR.parent / "history.json"

CHANNEL             = "lvivych_news"
OBLEN_CHANNEL       = "lvivsvitlo"
POLL_SECONDS_NORMAL = 15
POLL_SECONDS_ALERT  = 3
OBLEN_POLL_SECONDS  = 60
POSITIVE_INTERVAL   = 10 * 60
POSITIVE_MAX_AGE    = 24 * 3600
WARN_MINUTES        = 15
QUIET_START         = 22
QUIET_END           = 9
WEATHER_SEND_HOURS  = (10, 21)    # Kyiv time: надсилаємо прогноз о 10:00 і 21:00
RAIN_PROB_THRESHOLD = 40          # % імовірності дощу щоб вважати "буде дощ"
LVIV_LAT            = 49.8397
LVIV_LON            = 24.0297
_KYIV_TZ            = ZoneInfo("Europe/Kyiv")

OBLEN_GROUP_ROWS = [["1.1","1.2"],["2.1","2.2"],["3.1","3.2"],
                    ["4.1","4.2"],["5.1","5.2"],["6.1","6.2"]]

TOKEN      = TOKEN_FILE.read_text().strip() if TOKEN_FILE.exists() else ""
GROQ_KEY   = GROQ_KEY_FILE.read_text().strip() if GROQ_KEY_FILE.exists() else ""

def _read_key(path: Path) -> str:
    raw = path.read_text().strip()
    if "=" in raw and not raw.startswith("sk-"):
        raw = raw.split("=", 1)[1].strip().strip('"').strip("'")
    return raw

CLAUDE_KEY = _read_key(CLAUDE_KEY_FILE) if CLAUDE_KEY_FILE.exists() else ""
API        = f"https://api.telegram.org/bot{TOKEN}"

DONATE_CARD = "4441 1110 3446 3160"
DONATE_TEXT = f"☕ Підтримати розробника (Monobank):\n<code>{DONATE_CARD}</code>"

# Reply keyboard — постійно видима панель кнопок знизу
BTN_ALERTS   = "🚨 Сповіщення 24"
BTN_NEWS     = "💥 Новини 24"
BTN_SCHEDULE = "⚡ Графік"
BTN_WEATHER  = "🌧 Опади"
BTN_SETTINGS = "⚙️ Налаштування"
BTN_DONATE   = "☕"

MAIN_KEYBOARD = {
    "keyboard": [
        [{"text": BTN_ALERTS}, {"text": BTN_NEWS}, {"text": BTN_SCHEDULE}, {"text": BTN_WEATHER}],
        [{"text": BTN_SETTINGS}, {"text": BTN_DONATE}],
    ],
    "resize_keyboard": True,
    "is_persistent": True,
}

# ── Telegram helpers ──────────────────────────────────────────────────────────

def tg(method: str, **kwargs) -> dict:
    try:
        r = requests.post(f"{API}/{method}", json=kwargs, timeout=10)
        return r.json()
    except Exception as e:
        log.warning("tg %s failed: %s", method, e)
        return {}


def send(chat_id: int, text: str, **kwargs) -> None:
    kwargs.setdefault("parse_mode", "HTML")
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
        points = []
        for deg in range(0, 361, 10):
            rad = math.radians(deg)
            dlat = (radius_km / 111) * math.cos(rad)
            dlon = (radius_km / (111 * math.cos(math.radians(lat)))) * math.sin(rad)
            points.append((lon + dlon, lat + dlat))
        m.add_line(Line(points, "#FF4444", 3))
        m.add_marker(CircleMarker((lon, lat), "#FF4444", 10))
        img = m.render(zoom=11)
        buf = io.BytesIO()
        img.save(buf, format="PNG")
        buf.seek(0)
        requests.post(f"{API}/sendPhoto", data={"chat_id": chat_id},
                      files={"photo": ("map.png", buf, "image/png")}, timeout=15)
    except Exception as e:
        log.warning("map generation failed: %s", e)

# ── Subscribers ───────────────────────────────────────────────────────────────

def load_subs() -> dict:
    if SUBS_FILE.exists():
        return json.loads(SUBS_FILE.read_text())
    return {}


def save_subs(subs: dict) -> None:
    SUBS_FILE.write_text(json.dumps(subs, ensure_ascii=False, indent=2))


def get_sub(chat_id: int) -> dict:
    return load_subs().get(str(chat_id), {})


def update_sub(chat_id: int, **fields) -> dict:
    subs = load_subs()
    sub  = subs.get(str(chat_id), {})
    sub.update(fields)
    subs[str(chat_id)] = sub
    save_subs(subs)
    return sub


def load_positive_sent() -> set:
    if POSITIVE_SENT_FILE.exists():
        return set(json.loads(POSITIVE_SENT_FILE.read_text()))
    return set()


def save_positive_sent(sent: set) -> None:
    POSITIVE_SENT_FILE.write_text(json.dumps(list(sent)[-2000:]))


# ── Alerts log (24h history) ──────────────────────────────────────────────────

def load_alerts_log() -> list:
    if ALERTS_LOG_FILE.exists():
        return json.loads(ALERTS_LOG_FILE.read_text())
    return []


def append_alert_log(text: str, zones: list[str]) -> None:
    log_entries = load_alerts_log()
    cutoff = time.time() - 25 * 3600  # зберігаємо трохи більше 24h
    log_entries = [e for e in log_entries if e["ts"] >= cutoff]
    log_entries.append({"ts": time.time(), "text": text, "zones": zones})
    ALERTS_LOG_FILE.write_text(json.dumps(log_entries, ensure_ascii=False))


# ── Weather ───────────────────────────────────────────────────────────────────

def load_weather_notified() -> dict:
    if WEATHER_NOTIFIED_FILE.exists():
        return json.loads(WEATHER_NOTIFIED_FILE.read_text())
    return {}


def save_weather_notified(data: dict) -> None:
    WEATHER_NOTIFIED_FILE.write_text(json.dumps(data, ensure_ascii=False))


def fetch_weather() -> dict | None:
    try:
        r = requests.get(
            "https://api.open-meteo.com/v1/forecast",
            params={
                "latitude": LVIV_LAT, "longitude": LVIV_LON,
                "hourly": "temperature_2m,precipitation_probability,precipitation,weathercode",
                "forecast_days": 2,
                "timezone": "Europe/Kyiv",
            },
            timeout=10,
        )
        r.raise_for_status()
        return r.json()
    except Exception as e:
        log.warning("weather fetch failed: %s", e)
        return None


# WMO weather code → short Ukrainian label
_WMO = {
    0: "ясно ☀️", 1: "малохмарно 🌤", 2: "мінлива хмарність ⛅", 3: "хмарно ☁️",
    45: "туман 🌫", 48: "туман 🌫",
    51: "мряка 🌦", 53: "мряка 🌦", 55: "мряка 🌦",
    61: "невеликий дощ 🌧", 63: "дощ 🌧", 65: "сильний дощ 🌧",
    71: "сніг 🌨", 73: "сніг 🌨", 75: "сильний сніг 🌨",
    80: "зливи 🌦", 81: "зливи 🌦", 82: "зливи 🌦",
    95: "гроза ⛈", 96: "гроза з градом ⛈", 99: "гроза з градом ⛈",
}


_SNOW_CODES   = {71, 73, 75, 77, 85, 86}
_PRECIP_EMOJI = {"rain": "🌧", "snow": "🌨", "mixed": "🌨"}
_PRECIP_UA    = {"rain": "дощ", "snow": "сніг", "mixed": "дощ зі снігом"}


def _weather_label(code: int) -> str:
    return _WMO.get(code, "—")


def _precip_type(window_codes: list[int]) -> str:
    if not window_codes:
        return "rain"
    snow = sum(1 for c in window_codes if c in _SNOW_CODES)
    if snow == len(window_codes):
        return "snow"
    if snow > 0:
        return "mixed"
    return "rain"


def format_weather_24h(data: dict) -> str:
    hourly = data.get("hourly", {})
    times  = hourly.get("time", [])
    temps  = hourly.get("temperature_2m", [])
    probs  = hourly.get("precipitation_probability", [])
    codes  = hourly.get("weathercode", [])

    now = datetime.now(_KYIV_TZ).replace(minute=0, second=0, microsecond=0)
    lines = ["🌦 <b>Погода у Львові — наступні 24 години</b>\n"]

    windows, w_start, w_end, w_codes = [], None, None, []

    for i, t_str in enumerate(times):
        t    = datetime.fromisoformat(t_str).replace(tzinfo=_KYIV_TZ)
        prob = probs[i] if i < len(probs) else 0
        code = codes[i] if i < len(codes) else 0
        temp = temps[i] if i < len(temps) else None

        if t < now or t >= now + timedelta(hours=24):
            continue

        if t == now and temp is not None:
            lines.append(f"Зараз: {temp:.0f}°C, {_weather_label(code)}\n")

        if prob >= RAIN_PROB_THRESHOLD:
            if w_start is None:
                w_start = t
            w_end = t + timedelta(hours=1)
            w_codes.append(code)
        else:
            if w_start is not None:
                windows.append((w_start, w_end, _precip_type(w_codes)))
                w_start, w_codes = None, []

    if w_start is not None:
        windows.append((w_start, w_end, _precip_type(w_codes)))

    if windows:
        lines.append("<b>Очікуються опади:</b>")
        for start, end, ptype in windows:
            emoji = _PRECIP_EMOJI[ptype]
            label = _PRECIP_UA[ptype]
            lines.append(f"  • {emoji} {label}: {start.strftime('%H:%M')}–{end.strftime('%H:%M')}")
    else:
        lines.append("☀️ Опадів не очікується протягом 24 годин")

    return "\n".join(lines)


def precip_expected_24h(data: dict) -> list[tuple[datetime, datetime, str]]:
    """Повертає вікна опадів (start, end, type) в наступні 24h."""
    hourly = data.get("hourly", {})
    times  = hourly.get("time", [])
    probs  = hourly.get("precipitation_probability", [])
    codes  = hourly.get("weathercode", [])

    now    = datetime.now(_KYIV_TZ).replace(minute=0, second=0, microsecond=0)
    windows, w_start, w_end, w_codes = [], None, None, []

    for i, t_str in enumerate(times):
        t    = datetime.fromisoformat(t_str).replace(tzinfo=_KYIV_TZ)
        prob = probs[i] if i < len(probs) else 0
        code = codes[i] if i < len(codes) else 0
        if t < now or t >= now + timedelta(hours=24):
            if w_start:
                windows.append((w_start, w_end, _precip_type(w_codes)))
                w_start, w_codes = None, []
            continue
        if prob >= RAIN_PROB_THRESHOLD:
            if w_start is None:
                w_start = t
            w_end = t + timedelta(hours=1)
            w_codes.append(code)
        else:
            if w_start:
                windows.append((w_start, w_end, _precip_type(w_codes)))
                w_start, w_codes = None, []

    if w_start:
        windows.append((w_start, w_end, _precip_type(w_codes)))
    return windows


def get_alerts_24h(zone: str) -> list[dict]:
    cutoff = time.time() - 24 * 3600
    return [
        e for e in load_alerts_log()
        if e["ts"] >= cutoff
        and (not zone or zone == "all"
             or "all" in e.get("zones", [])
             or zone in e.get("zones", []))
    ]

# ── Oblenergo state ───────────────────────────────────────────────────────────

def load_oblen_state() -> dict:
    if OBLEN_STATE_FILE.exists():
        return json.loads(OBLEN_STATE_FILE.read_text())
    return {}


def save_oblen_state(state: dict) -> None:
    OBLEN_STATE_FILE.write_text(json.dumps(state, ensure_ascii=False, indent=2))


def load_warned() -> set:
    if OBLEN_WARNED_FILE.exists():
        return set(json.loads(OBLEN_WARNED_FILE.read_text()))
    return set()


def save_warned(warned: set) -> None:
    OBLEN_WARNED_FILE.write_text(json.dumps(list(warned)))

# ── Keyboards ─────────────────────────────────────────────────────────────────

def settings_keyboard(sub: dict) -> dict:
    zone       = sub.get("zone")
    positive   = sub.get("positive", False)
    oblen_grp  = sub.get("oblen_group")

    if zone:
        short = ZONES[zone]["name"].split("(")[0].strip()
        alerts_lbl = f"🚨 Сповіщення: {short} ✅"
    else:
        alerts_lbl = "🚨 Сповіщення про події — вимкнено"

    pos_lbl  = "💥 Позитивні новини ✅" if positive else "💥 Позитивні новини — вимкнено"

    if oblen_grp:
        g_lbl = "Весь Львів" if oblen_grp == "all" else f"Гр. {oblen_grp}"
        sched_lbl = f"⚡ Графік відключень: {g_lbl} ✅"
    else:
        sched_lbl = "⚡ Графік відключень — вимкнено"

    weather_lbl = "🌧 Сповіщення про опади ✅" if sub.get("weather") \
                  else "🌧 Сповіщення про опади — вимкнено"

    return {"inline_keyboard": [
        [{"text": alerts_lbl,  "callback_data": "cfg:alerts"}],
        [{"text": pos_lbl,     "callback_data": "cfg:positive"}],
        [{"text": sched_lbl,   "callback_data": "cfg:schedule"}],
        [{"text": weather_lbl, "callback_data": "cfg:weather"}],
        [{"text": "🔕 Відписатися від усього", "callback_data": "cfg:unsubscribe"}],
        [{"text": "☕ Подяка розробнику",       "callback_data": "donate"}],
    ]}


def zone_picker_keyboard(current: str | None, back_cb: str) -> dict:
    rows = []
    for key, zone in ZONES.items():
        mark = " ✅" if key == current else ""
        rows.append([{"text": zone["name"] + mark, "callback_data": f"zone:{key}"}])
    if current:
        rows.append([{"text": "🔕 Вимкнути сповіщення", "callback_data": "zone:off"}])
    rows.append([{"text": "🔙 Назад", "callback_data": back_cb}])
    return {"inline_keyboard": rows}


def group_picker_keyboard(current: str | None, back_cb: str) -> dict:
    rows = []
    for pair in OBLEN_GROUP_ROWS:
        row = []
        for g in pair:
            mark = " ✅" if g == current else ""
            row.append({"text": g + mark, "callback_data": f"oblen:{g}"})
        rows.append(row)
    all_mark = " ✅" if current == "all" else ""
    rows.append([{"text": f"🌍 Весь Львів{all_mark}", "callback_data": "oblen:all"}])
    if current:
        rows.append([{"text": "🔕 Вимкнути графік", "callback_data": "oblen:off"}])
    rows.append([{"text": "🔙 Назад", "callback_data": back_cb}])
    return {"inline_keyboard": rows}


def yes_no_keyboard(yes_cb: str, no_cb: str) -> dict:
    return {"inline_keyboard": [
        [{"text": "✅ Так", "callback_data": yes_cb}],
        [{"text": "❌ Ні, пропустити", "callback_data": no_cb}],
    ]}

# ── Onboarding steps ──────────────────────────────────────────────────────────

def onboard_welcome(chat_id: int) -> None:
    send(chat_id,
        "👋 <b>Привіт! Це Львів Бот.</b>\n\n"
        "Ось що він вміє:\n\n"
        "🚨 <b>Сповіщення для твого району</b> — фільтрує новини з @lvivych_news "
        "і надсилає тільки ті, що стосуються твоєї частини Львова: "
        "тривоги, відключення, аварії\n\n"
        "💥 <b>Позитивні новини</b> — перемоги ЗСУ, втрати ворога, удари по рф\n\n"
        "⚡ <b>Графік відключень світла</b> — актуальний розклад для твоєї групи "
        "з попередженням за 15 хвилин до відключення\n\n"
        "Налаштуємо?",
        reply_markup={"inline_keyboard": [
            [{"text": "Так, налаштувати →", "callback_data": "onboard:start"}]
        ]},
    )


def onboard_step_alerts(chat_id: int) -> None:
    send(chat_id,
        "🚨 <b>Крок 1 з 3 — Сповіщення по районах</b>\n\n"
        "Отримувати сповіщення про події у твоєму районі Львова?\n"
        "(тривоги, відключення, аварії, важливі новини регіону)",
        reply_markup=yes_no_keyboard("onboard:alerts_yes", "onboard:alerts_no"),
    )


def onboard_step_positive(chat_id: int) -> None:
    send(chat_id,
        "💥 <b>Крок 2 з 3 — Позитивні новини</b>\n\n"
        "Показувати новини про перемоги ЗСУ, втрати ворога і удари по рф?",
        reply_markup=yes_no_keyboard("onboard:positive_yes", "onboard:positive_no"),
    )


def onboard_step_weather(chat_id: int) -> None:
    send(chat_id,
        "🌧 <b>Крок 4 з 4 — Опади</b>\n\n"
        "Отримувати сповіщення про опади у Львові?\n\n"
        "Бот надсилатиме прогноз о <b>10:00</b> і <b>21:00</b> — чи буде дощ або сніг.",
        reply_markup=yes_no_keyboard("onboard:weather_yes", "onboard:weather_no"),
    )


def onboard_step_schedule(chat_id: int) -> None:
    send(chat_id,
        "⚡ <b>Крок 3 з 4 — Графік відключень</b>\n\n"
        "Отримувати графік відключень світла від Львівобленерго?\n\n"
        "Якщо так — обереш свою групу (є на квитанції або на "
        "<a href='https://www.loe.lviv.ua/'>loe.lviv.ua</a>). "
        f"Бот надсилатиме оновлення і попереджатиме за {WARN_MINUTES} хв до відключення.",
        reply_markup=yes_no_keyboard("onboard:schedule_yes", "onboard:schedule_no"),
    )


def onboard_done(chat_id: int) -> None:
    update_sub(chat_id, onboarded=True, onboarding_step=None)
    sub = get_sub(chat_id)
    # Показуємо постійну панель кнопок — один раз, далі вона просто висить
    send(chat_id,
        "✅ <b>Готово!</b> Кнопки внизу — завжди під рукою.\n"
        "Натисни будь-яку щоб змінити налаштування або побачити графік.",
        reply_markup=MAIN_KEYBOARD,
    )
    send(chat_id, "⚙️ <b>Поточні налаштування:</b>",
         reply_markup=settings_keyboard(sub))

# ── Groq AI filter ────────────────────────────────────────────────────────────

_THREAT_RE = re.compile(
    r"шахед|дрон|\bбпла\b|ракет|авіац|вибух|\bппо\b|\bзрк\b|обстріл|балістик"
    r"|відключ.{0,15}світл|знеструм|відключ.{0,15}електр"
    r"|відключ.{0,15}вод|без.{0,10}води|водопостач|гарячої вод|холодної вод"
    r"|відключ.{0,15}газ|газопостач|без.{0,10}газ"
    r"|\bопален\b|відключ.{0,15}тепл"
    r"|перекрит.{0,20}центр.{0,20}дорог|перекрит.{0,20}проспект|перекрит.{0,20}вулиц"
    r"|\bтривог|\bзбили\b|прямує в бік",
    flags=re.IGNORECASE,
)


def groq_is_relevant(text: str) -> bool:
    if not GROQ_KEY:
        return bool(_THREAT_RE.search(text))
    prompt = (
        "Повідомлення з новинного каналу Львова. "
        "Відповідай ТАК лише якщо повідомлення стосується ОДНОГО з: "
        "1) загроза з повітря — шахед, дрон, ракета, авіація, вибух, ппо, зрк, обстріл; "
        "2) відключення електрики у конкретному районі (де саме немає світла); "
        "3) відключення або аварія водопостачання у конкретному районі; "
        "4) відключення газу або опалення у конкретному районі. "
        "ВАЖЛИВО: подія має стосуватися Львова або Львівської області. "
        "Якщо подія в іншому регіоні України — НІ. "
        "Якщо це новини, ДТП, протести, кримінал, суд, культура, спорт, реклама — НІ. "
        "Відповідь одним словом: ТАК або НІ.\n\n"
        f"Повідомлення: {text[:400]}"
    )
    for attempt in range(3):
        try:
            r = requests.post(
                "https://api.groq.com/openai/v1/chat/completions",
                headers={"Authorization": f"Bearer {GROQ_KEY}", "Content-Type": "application/json"},
                json={"model": "qwen/qwen3.8-27b",
                      "messages": [{"role": "user", "content": prompt}],
                      "max_tokens": 200, "temperature": 0},
                timeout=10,
            )
            if r.status_code == 429:
                time.sleep(min(float(r.headers.get("retry-after", "1")), 3))
                continue
            r.raise_for_status()
            reply = r.json()["choices"][0]["message"]["content"].strip().upper()
            return "ТАК" in reply or "YES" in reply
        except Exception as e:
            log.warning("Groq failed (attempt %d): %s", attempt + 1, e)
            if attempt < 2:
                time.sleep(1)
    return bool(_THREAT_RE.search(text))

# ── Channel polling helpers (@lvivych_news) ───────────────────────────────────

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
    cutoff   = time.time() - POSITIVE_MAX_AGE
    fresh    = [e for e in history.values()
                if e.get("moods", {}).get("positive") and e.get("first_seen", 0) >= cutoff]
    sent     = load_positive_sent()
    new_items = [e for e in fresh if e["link"] not in sent]
    if not new_items:
        return
    subs = load_subs()
    positive_subs = [cid for cid, sub in subs.items()
                     if sub.get("positive") and sub.get("zone")]
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
        text  = f"💥 Втрати ворога і удари по рф\n\n{title}"
        if desc and desc != title:
            text += f"\n{desc}"
        if link:
            text += f"\n{link}"
        for cid in positive_subs:
            send(int(cid), text, disable_web_page_preview=False)
        sent.add(item["link"])
    save_positive_sent(sent)
    log.info("Positive news: %d item(s) → %d subs", len(new_items), len(positive_subs))


def fetch_messages() -> list[dict]:
    try:
        r = requests.get(f"https://t.me/s/{CHANNEL}", timeout=10,
                         headers={"User-Agent": "Mozilla/5.0"})
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
            if re.search(r'Підписати(ся)?', a.get_text(), re.IGNORECASE):
                a.decompose()
        text = text_el.get_text(" ", strip=True)
        text = re.sub(r'\s*\S.{0,40}\|\s*Підписати(ся)?\s*$', '', text, flags=re.IGNORECASE)
        text = re.sub(r'\s*\|?\s*Підписати(ся)?\s*$', '', text, flags=re.IGNORECASE)
        text = re.sub(r'\s*\S{1,30}\s*\|\s*$', '', text)
        text = re.sub(r'\s{2,}', ' ', text).strip()
        if msg_id and text:
            msgs.append({"id": msg_id, "text": text})
    return msgs


_PROMO_RE = re.compile(
    r"квитк[иіо]|internet-bilet|concert\.lviv|квиток\b|афіш[аи]"
    r"|\bдиктант\b|радіодиктант|фестиваль\b|\bконкурс\b|\bвистав[аи]\b|\bконцерт\b",
    flags=re.IGNORECASE,
)
_BROADCAST_RE = re.compile(
    r"повітряна тривог|повітряній тривоз|\bвідбій\b|дорозвідк"
    r"|\bппо\b|\bзрк\b|буде гучно|над містом|навколо львов|над львов"
    r"|балістик|загроза.*з (курськ|білорус|крим)|крилат.*ракет",
    flags=re.IGNORECASE,
)


def is_promo(text: str) -> bool:
    return bool(_PROMO_RE.search(text))


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
    return [key for key, zone in ZONES.items()
            if re.search("|".join(re.escape(s) for s in zone["stems"]),
                         text_lower, re.IGNORECASE)]

# ── Oblenergo: Claude Vision parsing ─────────────────────────────────────────

_OBLEN_PROMPT = """На зображенні — графік погодинних відключень електроенергії Львівобленерго.
Поверни JSON і нічого більше:
{"date":"DD.MM.YYYY","as_of":"HH:MM DD.MM.YYYY","groups":{"1.1":[["HH:MM","HH:MM"],...],...}}
Кожна пара — час початку і кінця відключення. Включи всі групи з зображення (1.1–6.2).
Відповідь — тільки JSON."""


def fetch_oblen_page() -> list[dict]:
    try:
        r = requests.get(f"https://t.me/s/{OBLEN_CHANNEL}", timeout=15,
                         headers={"User-Agent": "Mozilla/5.0"})
        r.raise_for_status()
    except Exception as e:
        log.warning("oblen fetch failed: %s", e)
        return []
    soup = BeautifulSoup(r.text, "html.parser")
    msgs = []
    for el in soup.select(".tgme_widget_message"):
        msg_id = el.get("data-post", "")
        if not msg_id:
            continue
        text_el   = el.select_one(".tgme_widget_message_text")
        text      = text_el.get_text(" ", strip=True) if text_el else ""
        photo_url = None
        wrap      = el.select_one(".tgme_widget_message_photo_wrap")
        if wrap:
            m = re.search(r"url\('([^']+)'\)", wrap.get("style", ""))
            if m:
                photo_url = m.group(1)
        msgs.append({"id": msg_id, "text": text, "photo_url": photo_url})
    return msgs


def parse_schedule_image(photo_url: str) -> dict | None:
    if not CLAUDE_KEY:
        log.warning("No Anthropic key")
        return None
    try:
        r = requests.get(photo_url, timeout=20, headers={"User-Agent": "Mozilla/5.0"})
        r.raise_for_status()
        image_b64  = base64.standard_b64encode(r.content).decode()
        media_type = r.headers.get("content-type", "image/jpeg").split(";")[0]
    except Exception as e:
        log.warning("image download failed: %s", e)
        return None
    try:
        client = anthropic.Anthropic(api_key=CLAUDE_KEY)
        resp   = client.messages.create(
            model="claude-haiku-4-5-20251001", max_tokens=1024,
            messages=[{"role": "user", "content": [
                {"type": "image", "source": {"type": "base64",
                                              "media_type": media_type, "data": image_b64}},
                {"type": "text", "text": _OBLEN_PROMPT},
            ]}],
        )
        raw = re.sub(r"^```[a-z]*\n?", "", resp.content[0].text.strip())
        raw = re.sub(r"\n?```$", "", raw)
        parsed = json.loads(raw)
        log.info("Schedule parsed: date=%s groups=%s",
                 parsed.get("date"), list(parsed.get("groups", {}).keys()))
        return parsed
    except Exception as e:
        log.warning("Claude parse failed: %s", e)
        return None

# ── Oblenergo: format & time ──────────────────────────────────────────────────

def fmt_group_block(group_id: str, periods: list) -> str:
    if not periods:
        return f"<b>Група {group_id}</b>\n✅ Відключень не заплановано"
    lines = [f"<b>Група {group_id}</b>", "⚡ Немає електрики:"]
    for start, end in periods:
        lines.append(f"  • {start} – {end}")
    return "\n".join(lines)


def fmt_full_schedule(schedule: dict) -> str:
    date  = schedule.get("date", "")
    as_of = schedule.get("as_of", "")
    header = f"⚡ <b>Графік відключень — {date}</b>"
    if as_of:
        header += f"\n<i>Інформація станом на {as_of}</i>"
    blocks = [fmt_group_block(g, schedule.get("groups", {}).get(g, []))
              for g in sorted(schedule.get("groups", {}))]
    return header + "\n\n" + "\n\n".join(blocks)


def fmt_group_schedule(group_id: str, schedule: dict) -> str:
    date  = schedule.get("date", "")
    as_of = schedule.get("as_of", "")
    header = f"⚡ <b>Графік відключень — {date}</b>"
    if as_of:
        header += f"\n<i>Станом на {as_of}</i>"
    return header + "\n\n" + fmt_group_block(group_id,
                                              schedule.get("groups", {}).get(group_id, []))


def next_outages_today(group_id: str, schedule: dict) -> list[tuple[datetime, datetime]]:
    date_str = schedule.get("date", "")
    if not date_str:
        return []
    try:
        base = datetime.strptime(date_str, "%d.%m.%Y")
    except ValueError:
        return []
    result = []
    for start_s, end_s in schedule.get("groups", {}).get(group_id, []):
        try:
            sh, sm = map(int, start_s.split(":"))
            if end_s == "24:00":
                end_dt = base.replace(hour=0, minute=0, second=0, microsecond=0) + timedelta(days=1)
            else:
                eh, em = map(int, end_s.split(":"))
                end_dt = base.replace(hour=eh, minute=em, second=0, microsecond=0)
            start_dt = base.replace(hour=sh, minute=sm, second=0, microsecond=0)
            if end_dt <= start_dt:
                end_dt += timedelta(days=1)
            result.append((start_dt, end_dt))
        except Exception:
            pass
    return result


def _warn_key(chat_id: int, group_id: str, start_s: str, date_s: str) -> str:
    return f"{chat_id}:{group_id}:{date_s}:{start_s}"


def is_quiet_hours() -> bool:
    h = datetime.now(_KYIV_TZ).hour
    return h >= QUIET_START or h < QUIET_END


def group_changed(group_id: str, old_schedule: dict | None, new_schedule: dict) -> bool:
    if old_schedule is None:
        return True
    return (old_schedule.get("groups", {}).get(group_id) !=
            new_schedule.get("groups", {}).get(group_id))

# ── Background loops ──────────────────────────────────────────────────────────

def warn_loop(lock: threading.Lock) -> None:
    log.info("Попередження: фоновий цикл запущено")
    while True:
        time.sleep(60)
        now      = datetime.now()
        state    = load_oblen_state()
        schedule = state.get("schedule")
        if not schedule:
            continue
        subs = load_subs()
        with lock:
            warned  = load_warned()
            changed = False
            for chat_id_s, sub in subs.items():
                g = sub.get("oblen_group")
                if not g or g == "all":
                    continue
                for start_dt, end_dt in next_outages_today(g, schedule):
                    mins = (start_dt - now).total_seconds() / 60
                    wkey = _warn_key(int(chat_id_s), g,
                                     start_dt.strftime("%H:%M"), schedule.get("date", ""))
                    if 13 <= mins <= 16 and wkey not in warned:
                        send(int(chat_id_s),
                             f"⏰ <b>Увага! Група {g}</b>\n"
                             f"За {WARN_MINUTES} хв відключення електрики\n"
                             f"🕐 <b>{start_dt.strftime('%H:%M')} – {end_dt.strftime('%H:%M')}</b>\n\n"
                             "Встигни зарядити пристрої!")
                        log.info("Попередження: chat=%s group=%s %s",
                                 chat_id_s, g, start_dt.strftime("%H:%M"))
                        warned.add(wkey)
                        changed = True
            if changed:
                save_warned(warned)


def weather_loop() -> None:
    log.info("Погода: фоновий цикл запущено (о 10:00 і 21:00)")
    notified = load_weather_notified()
    while True:
        time.sleep(60)
        now      = datetime.now(_KYIV_TZ)
        hour     = now.hour
        slot_key = f"{now.strftime('%Y-%m-%d')}-{hour}"

        if hour not in WEATHER_SEND_HOURS:
            continue
        if notified.get(slot_key):
            continue

        data = fetch_weather()
        if not data:
            continue

        windows = precip_expected_24h(data)
        subs    = load_subs()
        sent    = False

        for chat_id_s, sub in subs.items():
            if not sub.get("weather"):
                continue
            if windows:
                lines = []
                for s, e, ptype in windows:
                    emoji = _PRECIP_EMOJI[ptype]
                    label = _PRECIP_UA[ptype]
                    lines.append(f"{emoji} {label}: {s.strftime('%H:%M')}–{e.strftime('%H:%M')}")
                msg = "🌦 <b>Опади у Львові</b>\n\n" + "\n".join(lines) + "\n\nВіддяки прогнозу на 24 год."
            else:
                msg = "☀️ <b>Опади у Львові</b>\n\nОпадів не очікується найближчі 24 години."
            send(int(chat_id_s), msg)
            sent = True

        if sent:
            notified[slot_key] = True
            if len(notified) > 20:
                for k in sorted(notified)[:len(notified) - 10]:
                    del notified[k]
            save_weather_notified(notified)
            log.info("Погода: надіслано о %02d:00 (%s)", hour, "опади" if windows else "ясно")


def oblen_poll_loop() -> None:
    log.info("Поллінг @%s кожні %ds", OBLEN_CHANNEL, OBLEN_POLL_SECONDS)
    state    = load_oblen_state()
    seen_ids: set = set(state.get("seen_ids", []))
    msgs     = fetch_oblen_page()

    if not seen_ids:
        seen_ids = {m["id"] for m in msgs}
        state["seen_ids"] = list(seen_ids)
        log.info("Oblen: запам'ятовано %d постів", len(seen_ids))

    if not state.get("schedule"):
        last_photo = next((m for m in reversed(msgs) if m.get("photo_url")), None)
        if last_photo:
            log.info("Oblen: завантажую останній графік з %s", last_photo["id"])
            schedule = parse_schedule_image(last_photo["photo_url"])
            if schedule:
                state["schedule"] = schedule
                log.info("Oblen: графік завантажено — %s", schedule.get("date"))

    save_oblen_state(state)

    while True:
        time.sleep(OBLEN_POLL_SECONDS)
        msgs     = fetch_oblen_page()
        new_msgs = [m for m in msgs if m["id"] not in seen_ids]
        if not new_msgs:
            continue
        log.info("Oblen нових: %d", len(new_msgs))
        for msg in new_msgs:
            seen_ids.add(msg["id"])
            if not msg.get("photo_url"):
                continue
            schedule = parse_schedule_image(msg["photo_url"])
            if not schedule:
                log.warning("Не вдалося розпарсити графік %s", msg["id"])
                continue
            old_schedule = state.get("schedule")
            state["schedule"] = schedule
            state["seen_ids"] = list(seen_ids)
            save_oblen_state(state)
            save_warned(set())

            if is_quiet_hours():
                log.info("Oblen: тихий режим, сповіщення відкладено")
                continue

            subs = load_subs()
            for chat_id_s, sub in subs.items():
                g = sub.get("oblen_group")
                if not g:
                    continue
                if g != "all" and not group_changed(g, old_schedule, schedule):
                    log.info("Oblen: без змін для групи %s, chat=%s", g, chat_id_s)
                    continue
                try:
                    text = fmt_full_schedule(schedule) if g == "all" else fmt_group_schedule(g, schedule)
                    send(int(chat_id_s), text)
                except Exception as e:
                    log.warning("send oblen failed to %s: %s", chat_id_s, e)

        state["seen_ids"] = list(seen_ids)
        save_oblen_state(state)


def poll_loop() -> None:
    log.info("Поллінг @%s нормально=%ds тривога=%ds groq=%s",
             CHANNEL, POLL_SECONDS_NORMAL, POLL_SECONDS_ALERT, "✅" if GROQ_KEY else "❌")
    seen = load_seen()
    alert_active        = False
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
            if not broadcast and hit_zones:
                if not groq_is_relevant(msg["text"]):
                    log.info("Groq filtered out: %s", msg["text"][:80])
                    continue

            if broadcast:
                t = msg["text"].lower()
                if "відбій" in t and alert_active:
                    alert_active = False
                    log.info("Відбій — поллінг %ds", POLL_SECONDS_NORMAL)
                elif "відбій" not in t and not alert_active:
                    alert_active = True
                    log.info("Тривога — поллінг %ds", POLL_SECONDS_ALERT)

            log.info("broadcast=%s zones=%s | %s", broadcast, hit_zones, msg["text"][:80])
            # Логуємо для кнопки "Сповіщення 24"
            logged_zones = ["all"] if broadcast else hit_zones
            append_alert_log(msg["text"], logged_zones)

            subs = load_subs()
            for chat_id, sub in subs.items():
                zone = sub.get("zone")
                if not zone:
                    continue
                if broadcast:
                    send(int(chat_id), f"{broadcast_prefix(msg['text'])} {msg['text']}")
                elif "all" in hit_zones or zone == "all" or zone in hit_zones:
                    zone_name = ZONES.get(zone, {}).get("name", zone)
                    send(int(chat_id), f"⚠️ {zone_name}\n\n{msg['text']}")

        if new:
            save_seen(seen)

# ── Bot updates (long polling) ────────────────────────────────────────────────

def show_schedule(chat_id: int) -> None:
    state    = load_oblen_state()
    schedule = state.get("schedule")
    if not schedule:
        send(chat_id, "ℹ️ Графік ще не завантажено. Спробуй пізніше.")
        return
    sub = get_sub(chat_id)
    g   = sub.get("oblen_group")
    if g and g != "all":
        send(chat_id, fmt_group_schedule(g, schedule))
    else:
        send(chat_id, fmt_full_schedule(schedule))


def handle_update(update: dict) -> None:
    # ── Message ───────────────────────────────────────────────────────────────
    if msg := update.get("message"):
        chat_id = msg["chat"]["id"]
        text    = msg.get("text", "")

        if text.startswith("/start") or text.startswith("/menu"):
            sub = get_sub(chat_id)
            if sub.get("onboarded"):
                send(chat_id, "⚙️ <b>Налаштування</b>",
                     reply_markup=MAIN_KEYBOARD)
                send(chat_id, "⚙️ <b>Поточні налаштування:</b>",
                     reply_markup=settings_keyboard(sub))
            else:
                onboard_welcome(chat_id)

        elif text.startswith("/schedule") or text.startswith("/графік") or text == BTN_SCHEDULE:
            show_schedule(chat_id)

        elif text == BTN_ALERTS:
            sub     = get_sub(chat_id)
            zone    = sub.get("zone")
            entries = get_alerts_24h(zone)
            if not entries:
                send(chat_id, "🚨 За останні 24 години сповіщень не було.")
            else:
                header = f"🚨 <b>Сповіщення за 24 години</b> ({len(entries)} шт.)\n"
                chunks, cur = [], header
                for e in entries:
                    ts  = datetime.fromtimestamp(e["ts"], _KYIV_TZ).strftime("%H:%M")
                    blk = f"\n<b>{ts}</b> {e['text']}\n"
                    if len(cur) + len(blk) > 3800:
                        chunks.append(cur)
                        cur = blk
                    else:
                        cur += blk
                chunks.append(cur)
                for chunk in chunks:
                    send(chat_id, chunk)

        elif text == BTN_NEWS:
            if not HISTORY_FILE.exists():
                send(chat_id, "💥 Немає даних про новини.")
            else:
                try:
                    history = json.loads(HISTORY_FILE.read_text())
                except Exception:
                    history = {}
                cutoff = time.time() - 24 * 3600
                items  = sorted(
                    [e for e in history.values()
                     if e.get("moods", {}).get("positive") and e.get("first_seen", 0) >= cutoff],
                    key=lambda e: e.get("first_seen", 0),
                )
                if not items:
                    send(chat_id, "💥 За останні 24 години позитивних новин не знайдено.")
                else:
                    header = f"💥 <b>Позитивні новини за 24 години</b> ({len(items)} шт.)\n"
                    chunks, cur = [], header
                    for e in items:
                        ts    = datetime.fromtimestamp(e["first_seen"], _KYIV_TZ).strftime("%H:%M")
                        title = e.get("title", "").strip()
                        link  = e.get("link", "")
                        blk   = f"\n<b>{ts}</b> {title}"
                        if link:
                            blk += f"\n{link}"
                        blk += "\n"
                        if len(cur) + len(blk) > 3800:
                            chunks.append(cur)
                            cur = blk
                        else:
                            cur += blk
                    chunks.append(cur)
                    for chunk in chunks:
                        send(chat_id, chunk, disable_web_page_preview=False)

        elif text == BTN_WEATHER:
            data = fetch_weather()
            if not data:
                send(chat_id, "🌧 Не вдалося отримати прогноз опадів.")
            else:
                send(chat_id, format_weather_24h(data))

        elif text == BTN_SETTINGS:
            sub = get_sub(chat_id)
            send(chat_id, "⚙️ <b>Налаштування</b>",
                 reply_markup=settings_keyboard(sub))

        elif text == BTN_DONATE:
            send(chat_id, DONATE_TEXT,
                 reply_markup={"inline_keyboard": [
                     [{"text": "📋 Скопіювати", "callback_data": "copy_card"}]]})

    # ── Callback query ────────────────────────────────────────────────────────
    if cq := update.get("callback_query"):
        chat_id = cq["message"]["chat"]["id"]
        data    = cq.get("data", "")

        # ── Donate ───────────────────────────────────────────────────────────
        if data == "donate":
            answer(cq["id"])
            send(chat_id, DONATE_TEXT,
                 reply_markup={"inline_keyboard": [
                     [{"text": "📋 Скопіювати", "callback_data": "copy_card"}]]})

        elif data == "copy_card":
            answer(cq["id"], "✅ Скопійовано!")

        # ── Onboarding ───────────────────────────────────────────────────────
        elif data.startswith("onboard:"):
            step = data.split(":", 1)[1]
            answer(cq["id"])

            if step == "start":
                update_sub(chat_id, onboarding_step="alerts")
                onboard_step_alerts(chat_id)

            elif step == "alerts_yes":
                update_sub(chat_id, onboarding_step="zone")
                send(chat_id, "Обери свій район:",
                     reply_markup=zone_picker_keyboard(None, "onboard:alerts_no"))

            elif step == "alerts_no":
                update_sub(chat_id, zone=None, onboarding_step="positive")
                onboard_step_positive(chat_id)

            elif step == "positive_yes":
                update_sub(chat_id, positive=True, onboarding_step="schedule")
                onboard_step_schedule(chat_id)

            elif step == "positive_no":
                update_sub(chat_id, positive=False, onboarding_step="schedule")
                onboard_step_schedule(chat_id)

            elif step == "schedule_yes":
                update_sub(chat_id, onboarding_step="group")
                send(chat_id,
                    "Обери свою групу (знайди на квитанції або на "
                    "<a href='https://www.loe.lviv.ua/'>loe.lviv.ua</a>):",
                    reply_markup=group_picker_keyboard(None, "onboard:schedule_no"))

            elif step == "schedule_no":
                update_sub(chat_id, oblen_group=None, onboarding_step="weather")
                onboard_step_weather(chat_id)

            elif step == "weather_yes":
                update_sub(chat_id, weather=True)
                onboard_done(chat_id)

            elif step == "weather_no":
                update_sub(chat_id, weather=False)
                onboard_done(chat_id)

        # ── Zone selection ────────────────────────────────────────────────────
        elif data.startswith("zone:"):
            zone = data.split(":", 1)[1]
            answer(cq["id"])
            sub  = get_sub(chat_id)
            step = sub.get("onboarding_step")

            if zone == "off":
                update_sub(chat_id, zone=None)
                sub = get_sub(chat_id)
                send(chat_id, "🔕 Сповіщення вимкнено.",
                     reply_markup=settings_keyboard(sub))
            else:
                update_sub(chat_id, zone=zone)
                zone_name = ZONES[zone]["name"]
                if step == "zone":  # onboarding
                    update_sub(chat_id, onboarding_step="positive")
                    send(chat_id, f"✅ Район збережено: {zone_name}")
                    send_zone_map(chat_id, zone)
                    onboard_step_positive(chat_id)
                else:               # settings
                    sub = get_sub(chat_id)
                    send(chat_id, f"✅ Район: {zone_name}",
                         reply_markup=settings_keyboard(sub))
                    send_zone_map(chat_id, zone)
            log.info("chat_id=%s zone=%s", chat_id, zone)

        # ── Oblen group selection ─────────────────────────────────────────────
        elif data.startswith("oblen:"):
            group = data.split(":", 1)[1]
            answer(cq["id"])
            sub  = get_sub(chat_id)
            step = sub.get("onboarding_step")

            if group == "off":
                update_sub(chat_id, oblen_group=None)
                sub = get_sub(chat_id)
                send(chat_id, "🔕 Графік відключень вимкнено.",
                     reply_markup=settings_keyboard(sub))
            else:
                update_sub(chat_id, oblen_group=group)
                label = "🌍 Весь Львів" if group == "all" else f"Група {group}"
                if step == "group":  # onboarding → далі погода
                    update_sub(chat_id, onboarding_step="weather")
                    send(chat_id, f"✅ {label} збережено")
                    onboard_step_weather(chat_id)
                else:                 # settings
                    sub = get_sub(chat_id)
                    send(chat_id, f"✅ {label} збережено",
                         reply_markup=settings_keyboard(sub))
                    # показуємо поточний графік одразу
                    state    = load_oblen_state()
                    schedule = state.get("schedule")
                    if schedule:
                        txt = fmt_full_schedule(schedule) if group == "all" \
                              else fmt_group_schedule(group, schedule)
                        send(chat_id, txt)
            log.info("chat_id=%s oblen_group=%s", chat_id, group)

        # ── Settings panel ────────────────────────────────────────────────────
        elif data.startswith("cfg:"):
            action = data.split(":", 1)[1]
            answer(cq["id"])
            sub = get_sub(chat_id)

            if action == "alerts":
                send(chat_id, "🚨 <b>Сповіщення про події</b>\n\nОбери свій район:",
                     reply_markup=zone_picker_keyboard(sub.get("zone"), "cfg:back"))

            elif action == "positive":
                new_val = not sub.get("positive", False)
                update_sub(chat_id, positive=new_val)
                sub = get_sub(chat_id)
                label = "✅ Позитивні новини увімкнено" if new_val else "Позитивні новини вимкнено"
                send(chat_id, label, reply_markup=settings_keyboard(sub))

            elif action == "schedule":
                send(chat_id, "⚡ <b>Графік відключень</b>\n\nОбери свою групу:",
                     reply_markup=group_picker_keyboard(sub.get("oblen_group"), "cfg:back"))

            elif action == "weather":
                new_val = not sub.get("weather", False)
                update_sub(chat_id, weather=new_val)
                sub = get_sub(chat_id)
                label = "✅ Сповіщення про опади увімкнено" if new_val else "🔕 Сповіщення про опади вимкнено"
                send(chat_id, label, reply_markup=settings_keyboard(sub))

            elif action == "unsubscribe":
                subs = load_subs()
                subs.pop(str(chat_id), None)
                save_subs(subs)
                send(chat_id, "🔕 Відписано від усього.\n/start — налаштувати знову.")

            elif action == "back":
                sub = get_sub(chat_id)
                send(chat_id, "⚙️ <b>Налаштування</b>",
                     reply_markup=settings_keyboard(sub))

        # ── Positive toggle (legacy path) ─────────────────────────────────────
        elif data == "positive:toggle":
            answer(cq["id"])
            sub     = get_sub(chat_id)
            new_val = not sub.get("positive", False)
            update_sub(chat_id, positive=new_val)
            sub = get_sub(chat_id)
            send(chat_id,
                 "✅ Позитивні новини увімкнено" if new_val else "Позитивні новини вимкнено",
                 reply_markup=settings_keyboard(sub))


def updates_loop() -> None:
    offset = 0
    log.info("Слухаю оновлення бота...")
    while True:
        try:
            r = requests.post(f"{API}/getUpdates",
                              json={"offset": offset, "timeout": 30}, timeout=35)
            for upd in r.json().get("result", []):
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
        warn_lock = threading.Lock()
        threading.Thread(target=poll_loop,       daemon=True).start()
        threading.Thread(target=oblen_poll_loop, daemon=True).start()
        threading.Thread(target=warn_loop, args=(warn_lock,), daemon=True).start()
        threading.Thread(target=weather_loop,    daemon=True).start()
        updates_loop()
