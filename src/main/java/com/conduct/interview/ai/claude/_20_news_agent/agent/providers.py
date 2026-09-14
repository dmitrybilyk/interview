"""Єдина точка виклику LLM: call_llm(prompt) → str. Claude або Groq — залежно від PROVIDER."""

import logging
import os
import time

import requests
from anthropic import Anthropic

from .config import (
    CLAUDE_MODEL,
    GROQ_KEY_FILE_CANDIDATES,
    GROQ_MODEL,
    KEY_FILE_CANDIDATES,
    PROVIDER,
)

log = logging.getLogger("news_agent.providers")


def _load_key(env_var: str, candidates: list, human_name: str) -> str:
    if os.environ.get(env_var):
        return os.environ[env_var]
    for key_file in candidates:
        if key_file.exists():
            raw = key_file.read_text().strip()
            # handle "export VAR=value" shell format as well as a bare value
            if "=" in raw:
                raw = raw.split("=", 1)[1].strip()
            return raw
    raise RuntimeError(
        f"No {human_name} key found. Set {env_var} or create one of: "
        + ", ".join(str(c) for c in candidates)
    )


def _load_api_key() -> str:
    return _load_key("ANTHROPIC_API_KEY", KEY_FILE_CANDIDATES, "Anthropic API")


def _load_groq_key() -> str:
    return _load_key("GROQ_API_KEY", GROQ_KEY_FILE_CANDIDATES, "Groq API")


_client = None


def _client_lazy() -> Anthropic:
    """Lazy init — не падає при імпорті, якщо ключ відсутній."""
    global _client
    if _client is None:
        _client = Anthropic(api_key=_load_api_key())
    return _client


_KEEP_INDICES_SCHEMA = {"type": "array", "items": {"type": "integer"}}


def _call_claude(prompt: str, schema: dict | None = None) -> str:
    log.info("Calling Claude (%s)...", CLAUDE_MODEL)
    response = _client_lazy().messages.create(
        model=CLAUDE_MODEL,
        max_tokens=1024,
        # JSON schema гарантує формат відповіді — без нього LLM іноді обгортає масив у прозу.
        output_config={
            "format": {
                "type": "json_schema",
                "schema": schema or _KEEP_INDICES_SCHEMA,
            }
        },
        messages=[{"role": "user", "content": prompt}],
    )
    text = "".join(b.text for b in response.content if b.type == "text")
    log.info(
        "Claude responded (%d chars, %d input tokens, %d output tokens)",
        len(text), response.usage.input_tokens, response.usage.output_tokens,
    )
    return text


GROQ_MAX_RETRIES = 3


def _call_groq(prompt: str) -> str:

    payload = {
        "model": GROQ_MODEL,
        "messages": [{"role": "user", "content": prompt}],
        "max_tokens": 1024,

        "temperature": 0,
        # reasoning_effort=low: gpt-oss думає перед відповіддю — без обмеження з'їдає весь бюджет токенів.
        "reasoning_effort": "low",
    }
    headers = {"Authorization": f"Bearer {_load_groq_key()}"}

    for attempt in range(1, GROQ_MAX_RETRIES + 1):
        log.info("Calling Groq (%s), attempt %d/%d...", GROQ_MODEL, attempt, GROQ_MAX_RETRIES)
        resp = requests.post(
            "https://api.groq.com/openai/v1/chat/completions",
            headers=headers, json=payload, timeout=30,
        )
        if resp.status_code == 429 and attempt < GROQ_MAX_RETRIES:
            # Rate limit — чекаємо стільки, скільки каже retry-after.
            wait_seconds = float(resp.headers.get("retry-after", 10))
            log.warning("Groq rate-limited us — waiting %.0fs before retrying", wait_seconds)
            time.sleep(wait_seconds)
            continue
        resp.raise_for_status()
        break

    data = resp.json()
    text = data["choices"][0]["message"]["content"]
    usage = data.get("usage", {})
    log.info(
        "Groq responded (%d chars, %s prompt tokens, %s completion tokens)",
        len(text), usage.get("prompt_tokens"), usage.get("completion_tokens"),
    )
    return text


def call_llm(prompt: str, schema: dict | None = None) -> str:
    """Викликає Claude або Groq залежно від PROVIDER."""
    log.debug("PROVIDER=%s", PROVIDER)
    if PROVIDER == "groq":
        return _call_groq(prompt)
    return _call_claude(prompt, schema)
