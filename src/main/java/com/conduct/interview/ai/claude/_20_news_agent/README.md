# News Agent

A minimal example of using an LLM to *filter* content rather than just
generate it: fetch a live RSS feed, ask it to keep only the items that
match a mood, show the result in a one-page Flask UI — plus a Telegram
bot so people can subscribe instead of checking the page.

See **LEARNING.md** for how this project maps onto the other `claude/`
lessons and what to look at first if you're learning agents from it.

## Project layout — what each file teaches

```
agent/                 ← "the agent" itself, nothing else
  config.py              settings: provider, models, file paths, batch size
  fetch.py               perception: RSS -> plain dicts
  moods.py               policy: what counts as a match, in plain language
  providers.py            reasoning: call_llm(prompt) -> str (Claude or Groq)
  classify.py             orchestration: fetch + providers + moods -> result,
                           with the cache that avoids re-classifying old items
  history.py              a rolling log of past classified items, so /digest
                           can answer "what happened in the last N hours/days"
  __init__.py             re-exports the small public API the files above need

cli.py                 ← run the agent from a terminal, no web/Telegram
app.py                 ← Flask UI + Telegram webhook ("delivery" surface #1)
broadcaster.py         ← Telegram push, run by a timer ("delivery" surface #2)
telegram_bot.py         ← thin Telegram Bot API wrapper used by both of the above
templates/index.html   ← the page app.py renders
deploy.sh              ← ships all of the above to the production VM
```

The split matters more than it looks: `agent/` doesn't know Flask or
Telegram exist. Both delivery surfaces (`app.py`, `broadcaster.py`) call
the exact same `agent.get_filtered_news(mood)` — you could add a third
surface (a CLI cron job, a Slack bot, whatever) without touching `agent/`
at all. That's the pattern worth taking away, more than any single line
of code here.

It's a single LLM call per batch of new items, not a tool-use loop —
there's only one decision to make (keep or drop), and every item's
decision is independent, so a loop would add ceremony, not capability.
Compare with `_12_agent_loop/` for a case where multiple *dependent*
decisions (search, then check stock, then maybe order) actually need one.

### The three state files, and why each exists

| File | Lives in | Remembers | Why |
|---|---|---|---|
| `classification_cache.json` | `agent/classify.py` | LLM verdict + headline category per (article link, mood) | never ask the LLM about the same item twice — this is the main cost control |
| `sent_state.json` | `broadcaster.py` | which links were already pushed, per mood | never send a subscriber the same Telegram message twice |
| `history.json` | `agent/history.py` | title/link/category per item, keyed by first-seen time | lets `/digest` answer "what happened in the last 24h/7d" even though the live feed only ever shows the current ~80-item window |

The first two are pruned to whatever's still in the current feed;
`history.json` is pruned by age instead (`MAX_HISTORY_AGE_SECONDS` in
`config.py`, currently just over a week). All three are plain JSON files
(not a database — small enough not to need one), and all live **only on
the server** — `deploy.sh` never deletes or overwrites them (see its "no
`--delete`" comment).

### Headline categories (strike / losses / economy)

Alongside the keep-or-drop mood decision, `classify.py`'s `get_categories`
tags every kept "positive"/"mostly_positive" item with one of `strike`
(a Ukrainian/allied strike hitting a target *inside Russia*), `losses`
(confirmed Russian military losses), `economy` (Russian economic trouble),
or `other`. The rules live in `moods.CATEGORY_RULES`, right next to
`MOOD_RULES` — it's the same "one LLM call, cached per link" shape as mood
filtering, just answering a different question ("which of these three
story types, if any" instead of "keep or drop"). The category drives both
the colored badge on the web page (`templates/index.html`) and the emoji
prefix on Telegram pushes (`broadcaster.py`); it's skipped entirely for
mood="all" so that view keeps its zero-LLM-calls guarantee.

## LLM provider: Claude or Groq

`agent/providers.py`'s `call_llm(prompt) -> str` is the *only* place that
talks to a model. Everything else (fetch, cache, prompts) is identical
either way.

**Easiest way to switch while developing locally:** open
`agent/config.py` and change one line:

```python
DEFAULT_PROVIDER = "anthropic"  # change to "groq" and rerun — that's it
```

Or override per-run without editing anything:

```bash
LLM_PROVIDER=groq venv/bin/python cli.py positive
```

In production, `deploy.sh` sets the `LLM_PROVIDER` env var on the systemd
service (env var always wins over `DEFAULT_PROVIDER`), so editing
`config.py` has **zero effect on the deployed server** — it only changes
local runs where you haven't set the env var yourself.

Groq is called via its OpenAI-compatible `chat/completions` endpoint with
plain `requests` — no extra SDK needed. Default model is
`openai/gpt-oss-20b` (override with `GROQ_MODEL`); check
`https://api.groq.com/openai/v1/models` with your key if that model ever
gets retired. Two things specific to Groq's `gpt-oss` models that
`providers.py` handles for you:

- **`reasoning_effort: "low"`** — these are reasoning models that spend
  hidden "thinking" tokens before answering. Without capping effort, a
  big batch can spend its *entire* token budget thinking and return an
  empty answer.
- **Chunked classification** (`CLASSIFY_BATCH_SIZE` in `config.py`) — a
  cold start (empty cache) has to classify all ~80 feed items at once;
  sent as one giant prompt, that's enough reasoning tokens to either
  truncate the answer or trip a free-tier per-minute rate limit. Splitting
  into small batches keeps every individual call small and safe. Normal
  runs only ever see a handful of new items anyway, so this is usually a
  single batch in practice.
- **Retry with backoff on HTTP 429** — Groq's free tier has a small
  per-minute token budget; a burst of chunked calls can still trip it
  occasionally. `providers.py` reads the `retry-after` header Groq sends
  back and waits exactly that long before retrying (up to 3 attempts),
  instead of failing the whole request.

## Run locally

```bash
cd src/main/java/com/conduct/interview/ai/claude/_20_news_agent
python3 -m venv venv
venv/bin/pip install -r requirements.txt
```

**Fastest way to see a result** — no browser, no Telegram, just the
terminal:

```bash
venv/bin/python cli.py positive              # quiet — just the result
venv/bin/python cli.py positive --verbose    # + every fetch/cache/LLM step
venv/bin/python cli.py negative --verbose
LLM_PROVIDER=groq venv/bin/python cli.py positive --verbose   # try the other provider
```

`--verbose` is worth running at least once — it prints exactly what
`agent/classify.py` is doing: how many items were fetched, how many were
already cached, the batch(es) sent to the LLM, and how many it kept. This
is the same `logging` output that ends up in `journalctl` in production
(see below), just printed to your terminal instead.

**Full app, in the browser:**

```bash
venv/bin/python app.py
```

Open http://localhost:8600 and pick a mood — logs print to this terminal
as requests come in.

Keys are read from env vars first, else from local files (all gitignored):

| Purpose | Env var | Local file |
|---|---|---|
| Claude | `ANTHROPIC_API_KEY` | `../key.txt` (shared with other `claude/` lessons) |
| Groq | `GROQ_API_KEY` | `groq_key.txt` |
| Telegram bot | `TELEGRAM_BOT_TOKEN` | `telegram_token.txt` |

You only need the key for whichever provider you're actually using.
Telegram's webhook needs a public HTTPS URL, so `/start`-ing the bot
against your *local* run isn't practical — but `cli.py` and `app.py`
exercise the exact same `agent/` code the bot uses, so you don't need
Telegram to test the classification logic itself.

## Telegram subscription

Create a bot once via **@BotFather** on Telegram (`/newbot`), save the
token it gives you into `telegram_token.txt`. Once deployed:

- A user opens the bot and sends `/start` → picks 🙂/😟 via inline buttons
  → `app.py`'s `/telegram-webhook` route stores `{chat_id: mood}` in
  `subscribers.json` on the server.
- A systemd timer runs `broadcaster.py` every 15 minutes: for each mood
  with subscribers, it re-filters the feed and pushes only the items it
  hasn't sent before (tracked in `sent_state.json`) to those chat ids.
  The first run for a mood just records a baseline — new subscribers get
  news that appears *after* they subscribe, not the whole backlog. Each
  run also appends whatever it just classified to `history.json`.
- A subscriber sends `/digest` any time → picks "24 години" or "7 днів" via
  inline buttons → the bot reads `history.json` for their subscribed mood
  within that window and replies with counts per category plus a few
  example headlines each for strikes/losses/economy.

## Deploy

```bash
./deploy.sh
# or: LLM_PROVIDER=groq ./deploy.sh
```

This rsyncs the code (the whole `agent/` package plus the delivery files)
and whichever key files exist locally (`key.txt`, `groq_key.txt`,
`telegram_token.txt`) to the Oracle Cloud VM at `92.5.42.35`, sets up a
venv, and installs **two** systemd units:

- `news-agent.service` — the Flask app, bound to `127.0.0.1:8600` — **not**
  exposed directly.
- `news-agent-broadcast.timer` — runs `broadcaster.py` every 15 minutes.

`127.0.0.1`-only is deliberate: this VM's cloud security list only allows
ports 22/80/443 in (confirmed via `ss -tlnp` — nothing else is listening,
and `ufw` is inactive, so the block is at the cloud network layer, not the
OS). Opening an arbitrary port like 8600 would require a change in the
Oracle Cloud console (Security List / NSG) that can't be done over SSH.

Instead, the script adds a `location /news-agent/` block to the nginx
config that already terminates TLS on 443 for the planner app (the same
nginx that already serves `/remindly`), proxying it to
`127.0.0.1:8600`. So the app is reachable at:

**https://cozy-planner.duckdns.org/news-agent/**

The script is idempotent — it skips the nginx edit if the location is
already there, and always runs `nginx -t` before reloading. It also
registers the Telegram webhook (`setWebhook`) if `telegram_token.txt`
exists locally, and cleans up the old flat `news_agent.py` file (from
before this project was split into `agent/`) if it finds one on the server.

To check logs or restart manually on the server — this is the same
`logging` output `cli.py --verbose` prints locally:

```bash
ssh ubuntu@92.5.42.35
sudo systemctl status news-agent
sudo journalctl -u news-agent -f
sudo systemctl status news-agent-broadcast.timer
sudo journalctl -u news-agent-broadcast -f
```
