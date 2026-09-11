"""
PROVIDERS — the agent's "reasoning" step: send a prompt, get text back.

This is the piece a provider-agnostic agent needs: one function,
call_llm(prompt) -> str, that the rest of the code calls without caring
whether Claude or Groq answers. This same shape — a thin wrapper over
"send a prompt, get text back" — is what every agent framework builds on,
so it's worth seeing it with nothing else around it.

Switch providers by setting LLM_PROVIDER=groq (env var) or editing
DEFAULT_PROVIDER in config.py (easiest for local dev — see cli.py).
"""

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
            return key_file.read_text().strip()
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
    """Created on first use, not at import time — so importing this module
    never fails just because a key file is missing (e.g. Groq-only setups)."""
    global _client
    if _client is None:
        _client = Anthropic(api_key=_load_api_key())
    return _client


def _call_claude(prompt: str) -> str:
    log.info("Calling Claude (%s)...", CLAUDE_MODEL)
    response = _client_lazy().messages.create(
        model=CLAUDE_MODEL,
        max_tokens=1024,
        # This SDK's Claude generation has no `temperature` parameter (older
        # SDKs used temperature=0 for deterministic, "pick the right answer"
        # tasks like this one — see _1_basic_call's notes). Instead, we force
        # the *shape* of the answer with a JSON schema: the response is
        # guaranteed to be a plain JSON array, never prose wrapped around
        # one. That was the actual cause of a wrong cached verdict once (see
        # classify.py's cache) — not just an occasional format slip.
        output_config={
            "format": {
                "type": "json_schema",
                "schema": {"type": "array", "items": {"type": "integer"}},
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
    # Groq exposes an OpenAI-compatible chat/completions endpoint, so a plain
    # HTTP call is enough — no extra SDK dependency needed.
    payload = {
        "model": GROQ_MODEL,
        "messages": [{"role": "user", "content": prompt}],
        "max_tokens": 1024,
        # Same reasoning as Claude's temperature=0 below: this is a
        # deterministic classification task, not a creative one.
        "temperature": 0,
        # gpt-oss models spend tokens "thinking" before answering; without
        # this the reasoning alone can eat the whole max_tokens budget and
        # leave an empty final answer. Ask for a fast, low-effort answer
        # since this is a simple classification task, not hard reasoning.
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
            # Free-tier Groq keys have a small per-minute token budget
            # (e.g. 8000 TPM at time of writing). Classifying a big
            # cold-start backlog in quick chunks (see classify.py) can
            # burn through that in a few seconds — Groq tells us exactly
            # how long to wait via this header, so just wait that long
            # and try again instead of failing the whole request.
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


def call_llm(prompt: str) -> str:
    """The single entry point every caller uses — swapping PROVIDER in
    config.py (or LLM_PROVIDER env var) is the only thing that changes
    which branch runs."""
    log.debug("PROVIDER=%s", PROVIDER)
    if PROVIDER == "groq":
        return _call_groq(prompt)
    return _call_claude(prompt)
