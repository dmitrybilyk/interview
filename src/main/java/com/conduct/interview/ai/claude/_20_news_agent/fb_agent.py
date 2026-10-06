"""Facebook Messenger + Page Comments — агент від імені власника сторінки.

Обробляє два типи подій:
  • messaging  — приватні DM у Messenger
  • feed/changes — публічні коментарі під постами сторінки

Обидва режими використовують один LLM (Groq), але різні системні промпти:
  SYSTEM_PROMPT_DM      — детальні відповіді в Messenger
  SYSTEM_PROMPT_COMMENT — короткі, живі відповіді в коментарях

Налаштування через файли (не перезапускаючи сервіс):
  product_info.txt         — опис продукту/теми сторінки
  facebook_token.txt       — Page Access Token
  facebook_verify_token.txt — токен для верифікації webhook
  email_config.json        — SMTP для ескалації (тільки DM)
"""

import json
import logging
import os
import smtplib
import time
from email.mime.text import MIMEText
from pathlib import Path

import requests

from agent.providers import call_llm

log = logging.getLogger("news_agent.fb_agent")

HERE = Path(__file__).resolve().parent

PRODUCT_FILE       = HERE / "product_info.txt"
FB_TOKEN_FILE      = HERE / "facebook_token.txt"
FB_VERIFY_FILE     = HERE / "facebook_verify_token.txt"
EMAIL_CONFIG_FILE  = HERE / "email_config.json"

# ── Короткочасна пам'ять розмов (DM) ────────────────────────────────────────
_conversations: dict[str, list[dict]] = {}
_CONV_MAX_TURNS = 6
_CONV_TTL = 3600
_conv_timestamps: dict[str, float] = {}

# ref товару з якого прийшов юзер (з m.me?ref=...)
_product_refs: dict[str, str] = {}

# ── Системні промпти ─────────────────────────────────────────────────────────

SYSTEM_PROMPT_DM = """Ти — продавець-консультант, спілкуєшся від імені власника сторінки у Facebook Messenger.

Стиль: ввічливий, по-українськи, звертайся до покупця на «Ви». Природно, як жива людина — не робот.
Не починай кожне повідомлення з «Добрий день!». Веди розмову природно.

Нижче — опис продукту і точний процес замовлення. СТРОГО дотримуйся кроків замовлення коли людина хоче купити.
Збирай дані поступово (не питай все одразу).

Якщо питання про продукт але у тебе немає точної відповіді — відповідай рівно так: [ESCALATE]
Якщо спам / флуд / привітання без питання — відповідай дуже коротко.

{product_info}"""

SYSTEM_PROMPT_COMMENT = """Ти відповідаєш від імені власника Facebook-сторінки на публічні коментарі під постами.

Правила:
• Відповідай ДУЖЕ коротко — 1-3 речення максимум. Коментарі публічні, не треба довгих пояснень.
• Стиль ввічливий, по-українськи, звертайся на «Ви».
• Якщо питання про продукт/тему і ти не знаєш точної відповіді — запроси написати в Messenger: "Напишіть мені в особисті, детально відповім 🙏"
• Якщо це образа або негатив — відреагуй спокійно і конструктивно, не ігноруй.
• Якщо це просто лайк-коментар («Клас!», «👍») — подякуй одним реченням.
• НЕ починай відповідь з «Привіт» або «Дякуємо за коментар».

Інформація про сторінку/продукт:
---
{product_info}
---"""


# ── Допоміжні функції ────────────────────────────────────────────────────────

import re as _re

def _load_product_catalog() -> str:
    if PRODUCT_FILE.exists():
        return PRODUCT_FILE.read_text(encoding="utf-8").strip()
    return ""


def _extract_product(ref: str) -> str:
    """Витягує блок конкретного товару за його id з product_info.txt."""
    catalog = _load_product_catalog()
    pattern = rf"\[id:{_re.escape(ref)}\](.*?)\[/id:{_re.escape(ref)}\]"
    m = _re.search(pattern, catalog, _re.DOTALL)
    if m:
        return m.group(1).strip()
    return ""


def _list_products() -> list[str]:
    """Повертає список всіх id товарів з каталогу."""
    catalog = _load_product_catalog()
    return _re.findall(r"\[id:([^\]]+)\]", catalog)


def _load_product(ref: str | None = None) -> str:
    """Повертає інфо про товар: конкретний (якщо є ref) або весь каталог."""
    if ref:
        info = _extract_product(ref)
        if info:
            return info
    return _load_product_catalog() or "(опис продукту ще не додано)"


def _load_fb_token() -> str | None:
    if FB_TOKEN_FILE.exists():
        return FB_TOKEN_FILE.read_text().strip()
    return os.environ.get("FACEBOOK_PAGE_TOKEN")


def _load_verify_token() -> str:
    if FB_VERIFY_FILE.exists():
        return FB_VERIFY_FILE.read_text().strip()
    return "news_agent_fb_verify"


def _load_email_config() -> dict | None:
    if EMAIL_CONFIG_FILE.exists():
        return json.loads(EMAIL_CONFIG_FILE.read_text())
    return None


# ── Пам'ять розмови (тільки для DM) ─────────────────────────────────────────

def _get_history(sender_id: str) -> list[dict]:
    now = time.time()
    if sender_id in _conv_timestamps:
        if now - _conv_timestamps[sender_id] > _CONV_TTL:
            _conversations.pop(sender_id, None)
    _conv_timestamps[sender_id] = now
    return _conversations.setdefault(sender_id, [])


def _add_to_history(sender_id: str, role: str, content: str) -> None:
    history = _get_history(sender_id)
    history.append({"role": role, "content": content})
    if len(history) > _CONV_MAX_TURNS * 2:
        _conversations[sender_id] = history[-(_CONV_MAX_TURNS * 2):]


# ── LLM ─────────────────────────────────────────────────────────────────────

def _ask_llm_dm(sender_id: str, user_text: str) -> str:
    ref = _product_refs.get(sender_id)
    product_info = _load_product(ref)
    system = SYSTEM_PROMPT_DM.format(product_info=product_info.strip())
    history = _get_history(sender_id)

    messages_text = ""
    for msg in history:
        prefix = "Людина" if msg["role"] == "user" else "Я"
        messages_text += f"{prefix}: {msg['content']}\n"
    messages_text += f"Людина: {user_text}\nЯ:"

    return call_llm(f"{system}\n\nРозмова:\n{messages_text}").strip()


def _greet_no_ref(sender_id: str) -> str:
    """Привітання коли юзер прийшов без ref — питаємо що цікавить."""
    products = _list_products()
    if not products:
        return "Доброго дня! Чим можу допомогти? 😊"
    items = "\n".join(f"• {p}" for p in products)
    return f"Доброго дня! 👋 Що вас цікавить?\n\n{items}\n\nНапишіть назву або просто запитайте 😊"


def _ask_llm_comment(user_text: str) -> str:
    product_info = _load_product()
    system = SYSTEM_PROMPT_COMMENT.format(product_info=product_info)
    prompt = f"{system}\n\nКоментар: {user_text}\nМоя відповідь:"
    return call_llm(prompt).strip()


# ── Facebook Graph API ───────────────────────────────────────────────────────

def _send_dm(recipient_id: str, text: str) -> None:
    token = _load_fb_token()
    if not token:
        log.error("Facebook Page Token не налаштовано")
        return
    r = requests.post(
        "https://graph.facebook.com/v20.0/me/messages",
        params={"access_token": token},
        json={"recipient": {"id": recipient_id}, "message": {"text": text}},
        timeout=10,
    )
    if not r.ok:
        log.warning("FB sendMessage failed: %s", r.text[:300])
    else:
        log.info("FB DM sent to %s", recipient_id)


def _reply_to_comment(comment_id: str, text: str) -> None:
    token = _load_fb_token()
    if not token:
        log.error("Facebook Page Token не налаштовано")
        return
    r = requests.post(
        f"https://graph.facebook.com/v20.0/{comment_id}/comments",
        params={"access_token": token},
        json={"message": text},
        timeout=10,
    )
    if not r.ok:
        log.warning("FB replyComment failed (comment_id=%s): %s", comment_id, r.text[:300])
    else:
        log.info("FB comment reply posted to %s", comment_id)


def _get_sender_name(sender_id: str) -> str:
    token = _load_fb_token()
    if not token:
        return sender_id
    try:
        r = requests.get(
            f"https://graph.facebook.com/{sender_id}",
            params={"fields": "first_name,last_name", "access_token": token},
            timeout=5,
        )
        if r.ok:
            data = r.json()
            return f"{data.get('first_name', '')} {data.get('last_name', '')}".strip()
    except Exception:
        pass
    return sender_id


# ── Ескалація (тільки для DM) ────────────────────────────────────────────────

def _send_escalation_email(sender_id: str, user_text: str, profile_name: str) -> None:
    cfg = _load_email_config()
    if not cfg:
        log.warning("email_config.json відсутній — ескалацію пропущено")
        return
    try:
        body = (
            f"Питання від людини у Facebook Messenger:\n\n"
            f"Ім'я: {profile_name}\n"
            f"ID: {sender_id}\n\n"
            f"Повідомлення:\n{user_text}\n\n"
            f"--- бот не зміг відповісти ---"
        )
        msg = MIMEText(body, "plain", "utf-8")
        msg["Subject"] = f"❓ Facebook DM: питання від {profile_name}"
        msg["From"]    = cfg["smtp_user"]
        msg["To"]      = cfg["to"]
        with smtplib.SMTP(cfg["smtp_host"], cfg.get("smtp_port", 587)) as s:
            s.starttls()
            s.login(cfg["smtp_user"], cfg["smtp_password"])
            s.sendmail(cfg["smtp_user"], cfg["to"], msg.as_string())
        log.info("Ескалацію надіслано на %s", cfg["to"])
    except Exception:
        log.exception("Не вдалося надіслати email-ескалацію")


# ── Обробники подій ──────────────────────────────────────────────────────────

def _handle_messaging_event(event: dict) -> None:
    """Приватне повідомлення у Messenger."""
    sender_id = event.get("sender", {}).get("id")
    if not sender_id:
        return

    # Юзер прийшов через m.me?ref=... — запам'ятовуємо контекст товару
    referral = event.get("referral") or event.get("message", {}).get("referral")
    if referral and referral.get("ref"):
        _product_refs[sender_id] = referral["ref"]
        log.info("FB referral from %s: ref=%s", sender_id, referral["ref"])

    message = event.get("message", {})
    text = message.get("text", "").strip()
    if not text or message.get("is_echo"):
        return

    is_first_message = not _get_history(sender_id)

    # Якщо перше повідомлення і немає ref — вітаємось і питаємо що цікавить
    if is_first_message and sender_id not in _product_refs:
        greeting = _greet_no_ref(sender_id)
        _send_dm(sender_id, greeting)
        _add_to_history(sender_id, "assistant", greeting)
        # Тепер обробляємо і саме повідомлення юзера (може одразу написати що хоче)

    log.info("FB DM from %s: %.80s", sender_id, text)
    reply = _ask_llm_dm(sender_id, text)

    if "[ESCALATE]" in reply:
        name = _get_sender_name(sender_id)
        _send_escalation_email(sender_id, text, name)
        _send_dm(sender_id, "Зараз уточню деталі і повернусь до тебе найближчим часом 🙏")
        _add_to_history(sender_id, "user", text)
        _add_to_history(sender_id, "assistant", "[ескаловано]")
    else:
        _send_dm(sender_id, reply)
        _add_to_history(sender_id, "user", text)
        _add_to_history(sender_id, "assistant", reply)


def _handle_feed_change(change: dict) -> None:
    """Публічний коментар під постом сторінки."""
    value = change.get("value", {})

    # Тільки нові коментарі (не редагування, не видалення)
    if value.get("item") != "comment" or value.get("verb") != "add":
        return

    comment_id  = value.get("comment_id") or value.get("id")
    text        = value.get("message", "").strip()
    sender_name = value.get("from", {}).get("name", "")

    if not comment_id or not text:
        return

    log.info("FB comment from %s (id=%s): %.80s", sender_name, comment_id, text)
    reply = _ask_llm_comment(text)
    _reply_to_comment(comment_id, reply)


# ── Публічне API для web.py ──────────────────────────────────────────────────

def verify_webhook(mode: str, token: str, challenge: str) -> str | None:
    """Повертає challenge якщо verify_token збігається, інакше None."""
    if mode == "subscribe" and token == _load_verify_token():
        log.info("Facebook webhook verified")
        return challenge
    log.warning("Facebook webhook verification failed (token mismatch)")
    return None


def handle_webhook(data: dict) -> None:
    """Обробляє POST від Facebook (може містити кілька events)."""
    if data.get("object") != "page":
        return

    for entry in data.get("entry", []):
        # ── Messenger DM ──────────────────────────────────────────────────────
        for event in entry.get("messaging", []):
            _handle_messaging_event(event)

        # ── Коментарі під постами ─────────────────────────────────────────────
        for change in entry.get("changes", []):
            if change.get("field") == "feed":
                _handle_feed_change(change)
