# AI-концепції в News Agent

Два розділи: **використано** (є в коді) і **можна додати** (з поясненням навіщо).

---

## ✅ Що використовується

---

### 1. Базовий виклик LLM
`agent/providers.py` → `call_llm(prompt)`

Єдина точка входу до моделі. Ховає деталі провайдера від решти коду.

```python
def call_llm(prompt: str, schema: dict | None = None) -> str:
    if PROVIDER == "groq":
        return _call_groq(prompt)
    return _call_claude(prompt, schema)
```

Виклик Claude через офіційний SDK:

```python
response = _client_lazy().messages.create(
    model=CLAUDE_MODEL,       # claude-haiku-4-5-20251001
    max_tokens=1024,
    messages=[{"role": "user", "content": prompt}],
)
text = "".join(b.text for b in response.content if b.type == "text")
```

---

### 2. Structured output — JSON schema
`agent/providers.py`

LLM повертає масив індексів або категорій. Без схеми — модель іноді обгортає JSON у прозу ("Sure! Here are the indices: [0, 2]"), що ламає парсинг. JSON schema гарантує чистий JSON.

```python
_KEEP_INDICES_SCHEMA = {"type": "array", "items": {"type": "integer"}}

_CATEGORY_SCHEMA = {
    "type": "array",
    "items": {"type": "string", "enum": ["strike", "losses", "economy", "other"]},
}

response = client.messages.create(
    model=CLAUDE_MODEL,
    max_tokens=1024,
    output_config={
        "format": {
            "type": "json_schema",
            "schema": schema or _KEEP_INDICES_SCHEMA,
        }
    },
    messages=[{"role": "user", "content": prompt}],
)
```

---

### 3. Prompt Engineering — REJECT / KEEP
`agent/moods.py`

Найважливіша частина проекту. Якість фільтрації = якість промпту.

**Техніки:**
- **Zero-shot** — жодного прикладу, тільки правила
- **STEP 1 REJECT / STEP 2 KEEP** — спочатку відкидаємо, потім пропускаємо. Дає точніший результат ніж "пропусти якщо"
- **Explicit signal words** — перелік конкретних слів для кожного правила (без цього модель пропускає edge-cases)
- **"If in doubt → REJECT"** — за замовчуванням відкидаємо, не пропускаємо

```python
"positive": (
    "STEP 1 — REJECT IMMEDIATELY if any of these is true (no exceptions):\n"
    "X2. Any Ukrainian person killed, died, wounded. "
    "Signals: 'загинув', 'загинула', 'загинули', 'загибель', 'вбили', 'вбито', "
    "'поранений', 'є жертви' → REJECT.\n"
    "X6. Global oil prices RISING — benefits Russia. "
    "Signals: 'ціни на нафту зросли', 'нафта подорожчала' → REJECT.\n"
    "X7. Ukrainian domestic affairs — НАБУ, САП, суди, Рада → REJECT.\n\n"
    "STEP 2 — KEEP only if matches ONE of:\n"
    "K1. Morning losses report (Генштаб, specific death toll)\n"
    "K2. Russian economy getting worse\n"
    "K3. Ukrainian strike on Russian territory\n\n"
    "If in doubt → REJECT."
)
```

---

### 4. Batching (групування запитів)
`agent/classify.py` + `agent/config.py`

Замість одного HTTP-виклику на новину — кладемо 20 новин в один промпт. Швидше і дешевше.

```python
CLASSIFY_BATCH_SIZE = 20  # config.py
```

```python
# classify.py — нумерований список у промпті
numbered = "\n".join(
    f"{i}. {it['title']} — {it['description']}" for i, it in enumerate(batch)
)
prompt = f"Here is a numbered list of Ukrainian news items:\n\n{numbered}\n\nTask: {MOOD_RULES[mood]}\n\nRespond with ONLY a JSON array of indices to KEEP, e.g. [0,3,7]."
```

Модель повертає `[0, 3, 7]` — індекси новин що пройшли фільтр.

---

### 5. Local Result Caching (кешування результатів LLM)
`agent/classify.py` → `classification_cache.json`

RSS-стрічка майже не змінюється між запусками кожні 15 хв. Без кешу — платимо за кожну новину кожного разу. З кешем — LLM викликається тільки для нових посилань.

```python
cache = _load_classification_cache()
unknown = [it for it in items if mood not in cache.get(it["link"], {})]

if unknown:
    # викликаємо LLM тільки для нових items
    keep_in_batch = _classify_batch(batch, mood)
    for i, it in enumerate(batch):
        cache.setdefault(it["link"], {})[mood] = i in keep_in_batch
    _save_classification_cache(cache)

# результат — з кешу
result = [it for it in items if cache.get(it["link"], {}).get(mood)]
```

Ключ кешу: `{посилання: {mood: true/false, "_category": "strike"}}`. Один запис — один раз заплатили.

---

### 6. Dynamic Prompt Injection — Feedback Loop
`agent/feedback.py` + `agent/classify.py`

Коли користувач натискає "Не позитивна" — LLM генерує правило відхилення, яке автоматично додається в наступні промпти. Модель вчиться на фідбеку без перенавчання.

**Крок 1** — LLM генерує правило з фідбеку:
```python
prompt = (
    "A user reported this item as incorrectly included in the 'positive' feed.\n\n"
    f"Title: {title}\nDescription: {description}\n\n"
    "In ONE short phrase, describe the CATEGORY of news to reject in future.\n"
    "Format: 'News about [pattern]'. Reply with ONLY the rule."
)
rule = call_llm(prompt, schema=_RULE_SCHEMA)
# → "News about Ukrainian prisoner exchanges"
```

**Крок 2** — правило вставляється в наступні промпти класифікації:
```python
extra_rules = load_feedback_rules()
if extra_rules:
    lines = "\n".join(f"- {r}" for r in extra_rules)
    extra_section = f"\nUser-reported extra REJECT patterns:\n{lines}\n"

prompt = f"...\nTask: {MOOD_RULES[mood]}{extra_section}\n..."
```

---

### 7. Multi-Provider (взаємозамінні провайдери)
`agent/providers.py` + `agent/config.py`

Один і той самий агент запускається з Claude або з Groq (OpenAI-сумісний API). Перемикання через env-змінну без зміни коду.

```python
# config.py
PROVIDER = os.environ.get("LLM_PROVIDER", "anthropic").lower()
CLAUDE_MODEL = "claude-haiku-4-5-20251001"
GROQ_MODEL   = os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b")
```

```python
# Groq: reasoning_effort=low обмежує "внутрішні роздуми" моделі
payload = {
    "model": GROQ_MODEL,
    "temperature": 0,
    "reasoning_effort": "low",   # без цього reasoning-модель з'їдає весь бюджет токенів
    "messages": [{"role": "user", "content": prompt}],
}
```

Запуск: `LLM_PROVIDER=groq ./deploy.sh`

---

### 8. History / Memory
`agent/history.py`

RSS показує лише ~80 останніх новин (кілька годин). Щоб `/digest` міг відповісти "що за 7 днів" — broadcaster записує кожен запуск у файл.

```python
def record(mood: str, items: list[dict]) -> None:
    history = _load()
    for it in items:
        entry = history.setdefault(it["link"], {
            "title": it["title"], "link": it["link"],
            "moods": {}, "first_seen": time.time(),
        })
        entry["moods"][mood] = True
    # очищаємо записи старіші тижня
    cutoff = time.time() - MAX_HISTORY_AGE_SECONDS
    history = {l: e for l, e in history.items() if e["first_seen"] >= cutoff}
    _save(history)

def query(mood: str, hours: float) -> list[dict]:
    cutoff = time.time() - hours * 3600
    return sorted(
        [e for e in _load().values() if e["moods"].get(mood) and e["first_seen"] >= cutoff],
        key=lambda e: e["first_seen"], reverse=True
    )
```

---

## 💡 Що можна додати

---

### 9. Prompt Caching (Anthropic)
`../_8_prompt_caching/`

Зараз: MOOD_RULES (великий текст правил) надсилається з кожним запитом. Anthropic кешує prefixes промптів на 5 хвилин. Cache read коштує у 10 разів дешевше.

**Коли б це допомогло:** broadcaster запускається кожні 15 хв і класифікує по 20 новин у батчі. Правила (MOOD_RULES) незмінні — платимо за них кожен раз. З кешуванням платили б тільки за нові новини.

```python
# Як би виглядало в providers.py
response = client.messages.create(
    model=CLAUDE_MODEL,
    max_tokens=1024,
    system=[{
        "type": "text",
        "text": MOOD_RULES[mood],
        "cache_control": {"type": "ephemeral"}  # кешуємо правила на 5 хвилин
    }],
    messages=[{"role": "user", "content": numbered_news_batch}],
)
# usage.cache_read_input_tokens > 0 → cache hit, 10x дешевше
```

Економія: якщо MOOD_RULES ~800 токенів і broadcaster запускається 96 разів/день → ~77000 токенів/день лише на правила. З кешем — 800 токенів/день.

---

### 10. Streaming
`../_3_streaming/`

Зараз: веб-сторінка чекає поки LLM класифікує всі 80 новин і тільки потім рендерить. З streaming можна показувати новини одну за одною по мірі класифікації.

**Коли б це допомогло:** для веб-інтерфейсу — перші результати з'являлися б одразу, не через 3-5 секунд.

```python
# web.py + SSE endpoint
with client.messages.stream(
    model=CLAUDE_MODEL,
    messages=[{"role": "user", "content": prompt}],
) as stream:
    for text in stream.text_stream:
        yield f"data: {text}\n\n"   # Server-Sent Events
```

---

### 11. Tool Use / Function Calling
`../_5_tool_use/`

Зараз: агент пасивний — тільки читає RSS і класифікує. З tool use — Claude міг би сам запитувати додаткові дані коли потрібна перевірка.

**Приклад:** новина "ціни на нафту впали" — Claude викликає інструмент `get_oil_price()` щоб перевірити актуальну ціну перед включенням у feed.

```python
tools = [{
    "name": "get_current_oil_price",
    "description": "Returns current Brent crude oil price in USD",
    "input_schema": {"type": "object", "properties": {}, "required": []}
}]

response = client.messages.create(
    model=CLAUDE_MODEL,
    tools=tools,
    messages=[{"role": "user", "content": f"Is this news about oil prices accurate?\n{news_item}"}],
)

if response.stop_reason == "tool_use":
    tool_call = next(b for b in response.content if b.type == "tool_use")
    price = get_current_oil_price()  # наш Python-код
    # повертаємо результат моделі і продовжуємо
```

---

### 12. Extended Thinking (глибоке міркування)
`../_7_extended_thinking/`

Зараз: модель відповідає "тримай / відкидай" без пояснень. Для спірних новин — це чорна скринька.

**Коли б це допомогло:** режим налагодження — запустити thinking для прикордонних кейсів щоб зрозуміти чому модель прийняла рішення.

```python
response = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=8000,
    thinking={"type": "enabled", "budget_tokens": 2000},
    messages=[{"role": "user", "content": f"Should this be in 'positive' feed?\n{news_item}\n{MOOD_RULES['positive']}"}],
)

for block in response.content:
    if block.type == "thinking":
        print("INTERNAL REASONING:", block.thinking)   # видно логіку рішення
    elif block.type == "text":
        print("DECISION:", block.text)
```

---

### 13. Message Batches API (async)
`../_10_batches/`

Зараз: broadcaster класифікує 80 новин синхронно (чекає кожен HTTP-виклик). Batches API — асинхронна черга: відправив, прийшов за результатом пізніше.

**Коли б це допомогло:** якби потрібно класифікувати тисячі новин (архів, bulk-завдання). До 50% знижка на токени на деяких планах.

```python
batch = client.messages.batches.create(requests=[
    {"custom_id": item["link"], "params": {
        "model": CLAUDE_MODEL, "max_tokens": 256,
        "messages": [{"role": "user", "content": make_prompt(item)}]
    }}
    for item in items
])

# через кілька хвилин — забираємо результати
while client.messages.batches.retrieve(batch.id).processing_status != "ended":
    time.sleep(30)

for result in client.messages.batches.results(batch.id):
    verdict = parse_verdict(result.result.message.content[0].text)
    cache[result.custom_id] = verdict
```

---

### 14. RAG (Retrieval-Augmented Generation)
`../_4_rag/`

Зараз: кожна новина класифікується незалежно. RAG дозволив би знаходити схожі новини з минулого і давати моделі контекст "ось як ми класифікували подібне раніше".

**Коли б це допомогло:** few-shot приклади з реальної історії замість абстрактних правил — точніша класифікація для edge-cases.

```python
# знаходимо 3 найбільш схожі новини з history.json
similar = retrieve_similar(news_item, top_k=3)

examples = "\n".join(
    f"- '{ex['title']}' → {'KEEP' if ex['kept'] else 'REJECT'}" for ex in similar
)

prompt = f"""
{MOOD_RULES['positive']}

Examples from past decisions:
{examples}

Now classify: {news_item['title']} — {news_item['description']}
"""
```

---

### 15. Multi-turn (контекст розмови)
`../_6_multi_turn/`

Зараз: `/digest` — один запит, одна відповідь. З multi-turn — можна було б будувати інтерактивний дайджест: "покажи тільки удари", "розкажи більше про перший пункт".

```python
# history розмови зберігається між повідомленнями
messages = []

def chat(user_message: str) -> str:
    messages.append({"role": "user", "content": user_message})
    response = client.messages.create(
        model=CLAUDE_MODEL,
        system=f"You are a news digest assistant. Today's news:\n{digest_context}",
        messages=messages,
        max_tokens=512,
    )
    reply = response.content[0].text
    messages.append({"role": "assistant", "content": reply})
    return reply

chat("Що сталося сьогодні?")
chat("Розкажи більше про перший удар")   # модель пам'ятає попередній контекст
```

---

### 16. Multimodal (зображення)
`../_11_multimodal/`

Зараз: аналізуємо тільки текст (title + description). Деякі новини мають скріншоти з картами або графіки втрат.

**Коли б це допомогло:** класифікація скріншотів з Telegram-каналів — зображення теж несуть інформацію.

```python
import base64

with open("news_screenshot.png", "rb") as f:
    image_data = base64.b64encode(f.read()).decode()

response = client.messages.create(
    model=CLAUDE_MODEL,
    messages=[{"role": "user", "content": [
        {"type": "image", "source": {"type": "base64", "media_type": "image/png", "data": image_data}},
        {"type": "text",  "text": f"Is this image positive news for Ukraine? {MOOD_RULES['positive']}"},
    ]}],
)
```

---

## Зведена таблиця

| Концепція | Де в коді / папці | Статус |
|-----------|-------------------|--------|
| Базовий виклик LLM | `providers.py` | ✅ використовується |
| Structured output / JSON schema | `providers.py` | ✅ використовується |
| Prompt engineering (REJECT/KEEP) | `moods.py` | ✅ використовується |
| Batching запитів | `classify.py` | ✅ використовується |
| Local result caching | `classify.py` | ✅ використовується |
| Feedback → dynamic prompt | `feedback.py` | ✅ використовується |
| Multi-provider (Claude / Groq) | `providers.py` | ✅ використовується |
| History / Memory | `history.py` | ✅ використовується |
| Prompt Caching (Anthropic) | `../_8_prompt_caching` | 💡 можна додати |
| Streaming | `../_3_streaming` | 💡 можна додати |
| Tool Use | `../_5_tool_use` | 💡 можна додати |
| Extended Thinking | `../_7_extended_thinking` | 💡 можна додати |
| Message Batches API | `../_10_batches` | 💡 можна додати |
| RAG | `../_4_rag` | 💡 можна додати |
| Multi-turn | `../_6_multi_turn` | 💡 можна додати |
| Multimodal | `../_11_multimodal` | 💡 можна додати |
