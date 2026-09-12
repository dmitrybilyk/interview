"""
CLASSIFY — this is "the agent" itself: it ties fetch.py (perception),
providers.py (reasoning) and moods.py (the task) together into one
decision — "of these items, which match this mood?" — and remembers
answers so it never asks about the same item twice.

Why a single call, not a loop: an agent is a loop only when a task needs
MULTIPLE decisions that depend on each other (see ../../_12_agent_loop/ —
search, then check stock, then maybe order). Here there's exactly one
decision per item ("keep or drop"), and every item's decision is
independent of the others, so one batched call does the whole job. Adding
a loop on top would add ceremony, not capability.

THE CACHE, AND WHY IT MATTERS FOR COST:
The feed mostly repeats itself between checks — a 15-minute broadcaster
tick usually sees the same ~75 items plus a handful of new ones. Without
caching, every check would re-classify the entire batch, so cost scales
with (checks × feed size) instead of (checks × new items). The cache below
tracks, per article link, which moods it's already been checked against;
only items missing a verdict for the requested mood get sent to the LLM.
It's a plain JSON file (not a database) because it's small, and because
sharing it between two separate processes — the Flask app and the
Telegram broadcaster (broadcaster.py) — just means "read the same file",
no IPC needed.
"""

import json
import logging
import re

from .config import CLASSIFICATION_CACHE_FILE, CLASSIFY_BATCH_SIZE
from .fetch import fetch_items
from .moods import CATEGORY_RULES, MOOD_RULES
from .providers import call_llm

_CATEGORY_SCHEMA = {
    "type": "array",
    "items": {"type": "string", "enum": ["strike", "losses", "economy", "other"]},
}

log = logging.getLogger("news_agent.classify")


def _extract_json_array(text: str) -> list[int]:
    """The LLM sometimes wraps JSON in prose despite instructions — grab
    the array with a regex instead of failing on strict json.loads(text)."""
    match = re.search(r"\[[\d,\s]*\]", text)
    if not match:
        raise ValueError(f"No JSON array found in model output: {text[:200]}")
    return json.loads(match.group(0))


def _extract_category_array(text: str) -> list[str]:
    """Same idea as _extract_json_array, but the categorize call returns an
    array of strings (one per item), not integers, so it needs its own,
    more permissive regex."""
    match = re.search(r"\[.*\]", text, re.S)
    if not match:
        raise ValueError(f"No JSON array found in model output: {text[:200]}")
    return json.loads(match.group(0))


def _categorize_batch(batch: list[dict]) -> list[str]:
    """One LLM call tagging every item in `batch` with a headline category
    (see moods.CATEGORY_RULES) — independent of, and in addition to, the
    keep/drop decision in _classify_batch."""
    numbered = "\n".join(
        f"{i}. {it['title']} — {it['description']}" for i, it in enumerate(batch)
    )
    prompt = f"""Here is a numbered list of Ukrainian news items (title — description):

{numbered}

Task: {CATEGORY_RULES}"""

    text = call_llm(prompt, schema=_CATEGORY_SCHEMA)
    categories = _extract_category_array(text)
    if len(categories) != len(batch):
        raise ValueError(
            f"Expected {len(batch)} categories, got {len(categories)}: {text[:200]}"
        )
    return categories


def get_categories(items: list[dict]) -> dict[str, str]:
    """Tag each item with a headline category, used only to highlight/label
    items (see moods.CATEGORY_LABELS) — cached the same way as mood
    verdicts, under a "_category" key that can't collide with a mood name."""
    cache = _load_classification_cache()
    unknown = [it for it in items if "_category" not in cache.get(it["link"], {})]

    if unknown:
        log.info("%d/%d item(s) need a category tag", len(unknown), len(items))
        for start in range(0, len(unknown), CLASSIFY_BATCH_SIZE):
            batch = unknown[start:start + CLASSIFY_BATCH_SIZE]
            categories = _categorize_batch(batch)
            for it, category in zip(batch, categories):
                cache.setdefault(it["link"], {})["_category"] = category
        _save_classification_cache(cache)

    return {it["link"]: cache.get(it["link"], {}).get("_category", "other") for it in items}


def _classify_batch(batch: list[dict], mood: str) -> set[int]:
    """One LLM call for one small batch. Returns the set of *batch-local*
    indices to keep. Split out from pick_by_mood so that function's job —
    cache lookup, chunking, merging — stays readable on its own."""
    numbered = "\n".join(
        f"{i}. {it['title']} — {it['description']}" for i, it in enumerate(batch)
    )
    prompt = f"""Here is a numbered list of Ukrainian news items (title — description):

{numbered}

Task: {MOOD_RULES[mood]}

Respond with ONLY a JSON array of the integer indices to KEEP, e.g. [0,3,7].
No prose, no explanation, no markdown fences."""

    text = call_llm(prompt)
    return set(_extract_json_array(text))


def _load_classification_cache() -> dict:
    if CLASSIFICATION_CACHE_FILE.exists():
        return json.loads(CLASSIFICATION_CACHE_FILE.read_text())
    return {}


def _save_classification_cache(cache: dict) -> None:
    CLASSIFICATION_CACHE_FILE.write_text(json.dumps(cache, ensure_ascii=False, indent=2))


def pick_by_mood(items: list[dict], mood: str) -> list[dict]:
    """Return the subset of `items` that match `mood`, asking the LLM only
    about items with no cached verdict for this mood yet."""
    if mood not in MOOD_RULES:
        raise ValueError(f"Unknown mood: {mood!r}. Known moods: {list(MOOD_RULES)}")

    log.info("pick_by_mood(mood=%s) — %d candidate item(s)", mood, len(items))

    cache = _load_classification_cache()
    unknown = [it for it in items if mood not in cache.get(it["link"], {})]

    if not unknown:
        log.info("All %d item(s) already classified for mood=%s — skipping the LLM call entirely", len(items), mood)
    else:
        log.info("%d/%d item(s) are new for mood=%s — asking the LLM about just those", len(unknown), len(items), mood)

        # Chunk instead of sending all of `unknown` in one prompt: a giant
        # cold-start batch (e.g. the very first run, before any cache exists)
        # would make a reasoning model spend so many hidden "thinking" tokens
        # that it can truncate its own answer, or trip a provider's per-minute
        # token limit. Small, bounded chunks avoid both regardless of how big
        # the backlog gets.
        total_kept = 0
        for start in range(0, len(unknown), CLASSIFY_BATCH_SIZE):
            batch = unknown[start:start + CLASSIFY_BATCH_SIZE]
            log.info(
                "  classifying batch %d-%d of %d...",
                start, start + len(batch) - 1, len(unknown),
            )
            keep_in_batch = _classify_batch(batch, mood)
            for i, it in enumerate(batch):
                cache.setdefault(it["link"], {})[mood] = i in keep_in_batch
            total_kept += len(keep_in_batch)

        log.info("LLM kept %d/%d of the new item(s) for mood=%s", total_kept, len(unknown), mood)

        # Drop links that fell off the feed entirely, so the cache file
        # doesn't grow forever.
        current_links = {it["link"] for it in items}
        before = len(cache)
        cache = {link: verdicts for link, verdicts in cache.items() if link in current_links}
        if len(cache) != before:
            log.info("Pruned %d stale cache entr(y/ies) no longer in the feed", before - len(cache))

        _save_classification_cache(cache)

    result = [it for it in items if cache.get(it["link"], {}).get(mood)]
    log.info("Returning %d item(s) for mood=%s", len(result), mood)
    return result


def get_filtered_news(mood: str) -> list[dict]:
    """Convenience wrapper: fetch the live feed, then filter it.

    mood="all" is a special case handled here, before pick_by_mood: it
    means "no filtering", so there's nothing for an LLM to decide — the
    correct amount of AI to use for "give me everything" is none. This is
    the same idea as classify.py's cache, taken further: the cheapest
    correct answer is the one that skips the model call entirely."""
    items = fetch_items()
    if mood == "all":
        log.info("mood=all — no filtering, no LLM call, returning all %d fetched item(s)", len(items))
        return items

    result = pick_by_mood(items, mood)

    # Category tags are only meaningful for a filtered feed (they highlight
    # *why* something is positive) — computing them for mood="all" would
    # silently break its "zero LLM calls" guarantee for no benefit, since
    # nothing in that view is filtered on them anyway.
    categories = get_categories(result)
    for it in result:
        it["category"] = categories.get(it["link"], "other")

    return result
