"""
CONFIG — every knob this agent has, in one place.

Nothing in here talks to a network or an LLM. It's just constants, so you
can see (and change) the agent's behavior without hunting through logic
code. That split — "settings" vs "logic" — is worth internalizing early:
almost every real agent codebase has a config module like this one.
"""

import os
from pathlib import Path

# ── Easiest way to switch LLM provider while developing locally ────────────
# Just change this string and rerun (see cli.py). No env var needed.
#
# In production, deploy.sh sets the LLM_PROVIDER environment variable on the
# systemd service, and the env var always wins over this default — so
# editing this line has ZERO effect on the deployed server. It only changes
# what you get when you run things locally without setting LLM_PROVIDER
# yourself.
DEFAULT_PROVIDER = "anthropic"  # or "groq"

PROVIDER = os.environ.get("LLM_PROVIDER", DEFAULT_PROVIDER).lower()

# Which exact model each provider uses. Change these to try a different
# model without touching any calling code.
CLAUDE_MODEL = "claude-haiku-4-5-20251001"
# gpt-oss-20b, not the bigger 120b: this task is "yes/no per item", not hard
# reasoning, and the smaller model is faster and cheaper for it. Both are
# "reasoning" models under the hood (see providers.py's reasoning_effort
# note) — check https://api.groq.com/openai/v1/models with your key if this
# one ever gets retired.
GROQ_MODEL = os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b")

# How many items go into a single classification prompt. Reasoning models
# spend hidden "thinking" tokens proportional to how much they're asked to
# reason about — a single call covering all ~80 feed items risks either
# truncation (thinking eats the whole output budget) or tripping a
# provider's per-minute token limit. Chunking keeps every individual call
# small and safe regardless of how big the feed or the cold-start backlog
# gets. Most runs only ever see a handful of new items anyway (see
# classify.py's cache), so this rarely produces more than one chunk.
CLASSIFY_BATCH_SIZE = 20

# ── The feed we read ─────────────────────────────────────────────────────
# Same easy-switch pattern as DEFAULT_PROVIDER above: change this line (or
# set FETCH_SOURCE=rss) to flip sources with no other code changes.
#
# Trade-off, measured (see fetch.py): both fetch in ~0.35s, so "html" isn't
# actually faster — it's a straight fetch of censor.net's homepage instead
# of the dedicated RSS feed. It has real downsides: only ~30 items instead
# of RSS's 80, no separate description text (title only), and publish dates
# aren't reliably found for every item. Keep "rss" as the fallback in mind
# if classification quality or item count matters more than avoiding RSS.
DEFAULT_FETCH_SOURCE = "html"  # or "rss"
FETCH_SOURCE = os.environ.get("FETCH_SOURCE", DEFAULT_FETCH_SOURCE).lower()

RSS_URL = "https://assets.censor.net/rss/censor.net/rss_uk_news.xml"
HTML_URL = "https://censor.net/"
# The site 403s requests with no/default User-Agent header.
FETCH_HEADERS = {"User-Agent": "Mozilla/5.0 (compatible; news-agent/1.0)"}

# ── Where things live on disk ───────────────────────────────────────────
# This file is at .../_20_news_agent/agent/config.py, so:
PACKAGE_DIR = Path(__file__).resolve().parent   # .../_20_news_agent/agent
APP_DIR = PACKAGE_DIR.parent                     # .../_20_news_agent
CLAUDE_LESSONS_DIR = APP_DIR.parent              # .../claude  (shared key.txt lives here)

# Deployed (see deploy.sh), everything is flattened into one directory on
# the server, so key files sit next to app.py directly (APP_DIR). Locally,
# this project shares key.txt with the other claude/ lessons one level up
# (CLAUDE_LESSONS_DIR). Checking both makes the same code work in either
# layout with no changes.
KEY_FILE_CANDIDATES = [APP_DIR / "key.txt", CLAUDE_LESSONS_DIR / "key.txt"]
GROQ_KEY_FILE_CANDIDATES = [APP_DIR / "groq_key.txt", CLAUDE_LESSONS_DIR / "groq_key.txt"]

# Persistent state (NOT secrets, NOT code — see README's note on why
# deploy.sh is careful never to delete these on the server):
CLASSIFICATION_CACHE_FILE = APP_DIR / "classification_cache.json"
