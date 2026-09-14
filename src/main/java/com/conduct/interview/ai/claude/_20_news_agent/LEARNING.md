# Навчальний план: від _1_basic_call до цього агента

`_20_news_agent` — маленький підсумок: тут в одному реальному проєкті зустрічаються механіка API з ранніх уроків і продакшн-нюанси з пізніших.

## Порядок проходження уроків

### Фаза A — сирий API
| # | Тема | Головна думка |
|---|---|---|
| `_1_basic_call` | Базовий виклик | `messages.create()` → `response.content[0].text` |
| `_2_prompting` | Промптинг | Few-shot, system prompt, XML-теги — промпт і є програма |
| `_3_streaming` | Стрімінг | Токени приходять частинами; retry при `stop_reason == "max_tokens"` |
| `_6_multi_turn` | Мультитерн | Claude stateless — кожен раз надсилаєш усю історію |
| `_9_token_counting` | Токени | `count_tokens()` — безкоштовний dry-run перед дорогим запитом |

### Фаза B — більше можливостей
| # | Тема | Головна думка |
|---|---|---|
| `_4_rag` | RAG | Вставляй retrieved документи в промпт |
| `_5_tool_use` | Tool use | Claude просить запустити інструмент — ти запускаєш, повертаєш результат |
| `_8_prompt_caching` | Кешування промптів | `cache_control: ephemeral` на статичний текст — ~10x дешевше |
| `_10_batches` | Batch API | До 100k запитів, ~50% дешевше, асинхронно |

### Фаза C — агентний цикл
| # | Тема | Головна думка |
|---|---|---|
| `_12_agent_loop` | Agent loop | Цикл потрібен лише коли рішення залежать одне від одного |

### Фаза D — архітектура (читай, не запускай)
`_13` packaging · `_15` business→requirements · `_16` lifecycle · `_17` deployment · `_18` platforms · `_19` trust boundaries

---

## Де ці ідеї живуть в цьому агенті

| Ідея | Де в коді |
|---|---|
| `_1` базовий виклик | `providers.py` → `_call_claude()` |
| `_2` output constraints | `classify.py`: *"Respond with ONLY a JSON array"* |
| `_2` policy-as-text | `moods.py` — правила фільтрації на plain Ukrainian/English |
| `_6` stateless | Кожна класифікація — незалежний `messages.create()`, без history |
| `_7` extended thinking | Groq gpt-oss думає перед відповіддю → реальний баг: великий батч витрачав весь бюджет на роздуми і повертав порожній контент. Фікс: `reasoning_effort: "low"` + менші батчі |
| `_12` loop vs single call | Один рішення per item, незалежні → один батчевий виклик, не цикл |
| `_13` packaging | Весь `agent/` — це урок 13: відділи те, що не змінюється (`call_llm`) від того, що змінюється (промпт) |
| `_17` deployment | `deploy.sh`: systemd + nginx proxy замість відкритого порту (cloud security list блокує все крім 22/80/443) |
| `_19` trust boundary | **Не зафіксовано:** `/telegram-webhook` приймає POST від будь-кого. Вправа: додати перевірку `secret_token` з `setWebhook` |

## Вправи

1. **Додай новий mood** — тільки `moods.py`, нічого більше. Перевір `cli.py your-mood --verbose`.
2. **Prompt caching** — текст правил mood однаковий у всіх викликах → кандидат для `cache_control: ephemeral`.
3. **Закрий дірку _19** — перевірка підпису вебхука Telegram.
4. **Tool use** — дай агенту інструмент `fetch_full_article(url)`, щоб він сам вирішував, чи достатньо RSS-опису.
