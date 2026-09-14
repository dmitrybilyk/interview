"""Оркестрація: fetch → кеш → LLM → відфільтрований список.

Кеш (classification_cache.json) зберігає вердикт LLM per (посилання, mood).
Стрічка майже не змінюється між перевірками — без кешу LLM платимо за кожен запуск.
"""

import json
import logging
import re

from .config import CLASSIFICATION_CACHE_FILE, CLASSIFY_BATCH_SIZE
from .fetch import fetch_items
from .feedback import load_feedback_rules
from .moods import CATEGORY_RULES, MOOD_RULES
from .providers import call_llm

_CATEGORY_SCHEMA = {
    "type": "array",
    "items": {"type": "string", "enum": ["strike", "losses", "economy", "other"]},
}

log = logging.getLogger("news_agent.classify")


def _extract_json_array(text: str) -> list[int]:
    """LLM іноді обгортає JSON у прозу — витягуємо масив regex."""
    match = re.search(r"\[[\d,\s]*\]", text)
    if not match:
        raise ValueError(f"No JSON array found in model output: {text[:200]}")
    return json.loads(match.group(0))


def _extract_category_array(text: str) -> list[str]:
    """Те саме, але для масиву рядків (категорії)."""
    match = re.search(r"\[.*\]", text, re.S)
    if not match:
        raise ValueError(f"No JSON array found in model output: {text[:200]}")
    return json.loads(match.group(0))


def _categorize_batch(batch: list[dict]) -> list[str]:
    """Один виклик LLM: теґує кожен item категорією (strike/losses/economy/other)."""
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
    """Повертає {link: category} для списку items, використовує кеш."""
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
    """Один виклик LLM для батчу. Повертає індекси items які треба залишити."""
    numbered = "\n".join(
        f"{i}. {it['title']} — {it['description']}" for i, it in enumerate(batch)
    )
    extra_rules = load_feedback_rules()
    extra_section = ""
    if extra_rules:
        lines = "\n".join(f"- {r}" for r in extra_rules)
        extra_section = (
            f"\nUser-reported additional REJECT patterns (treat as extra Step 1 rules):\n"
            f"{lines}\n"
        )

    prompt = f"""Here is a numbered list of Ukrainian news items (title — description):

{numbered}

Task: {MOOD_RULES[mood]}{extra_section}

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
    """Повертає items що відповідають mood. LLM викликається лише для нових (не кешованих) items."""
    if mood not in MOOD_RULES:
        raise ValueError(f"Unknown mood: {mood!r}. Known moods: {list(MOOD_RULES)}")

    log.info("pick_by_mood(mood=%s) — %d candidate item(s)", mood, len(items))

    cache = _load_classification_cache()
    unknown = [it for it in items if mood not in cache.get(it["link"], {})]

    if not unknown:
        log.info("All %d item(s) already classified for mood=%s — skipping the LLM call entirely", len(items), mood)
    else:
        log.info("%d/%d item(s) are new for mood=%s — asking the LLM about just those", len(unknown), len(items), mood)

        # Батчуємо — reasoning-модель на великому промпті може вичерпати бюджет токенів.
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

        # Прибираємо зі кешу посилання яких вже немає в стрічці.
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
    """Завантажує стрічку і фільтрує за mood. mood="all" — без LLM, повертає все."""
    items = fetch_items()
    if mood == "all":
        log.info("mood=all — no filtering, no LLM call, returning all %d fetched item(s)", len(items))
        return items

    result = pick_by_mood(items, mood)

    # Категорії тільки для відфільтрованих — для mood="all" не потрібні.
    categories = get_categories(result)
    for it in result:
        it["category"] = categories.get(it["link"], "other")

    # Для positive — лише strike/losses/economy. "other" означає, що класифікатор
    # не знайшов відповідності K1/K2/K3, тому прибираємо.
    if mood == "positive":
        before = len(result)
        result = [it for it in result if it.get("category") != "other"]
        dropped = before - len(result)
        if dropped:
            log.info("mood=positive: відкинуто %d 'other' item(s) без категорії", dropped)

    return result
