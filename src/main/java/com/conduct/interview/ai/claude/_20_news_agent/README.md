# News Agent 🇺🇦

Фільтрує новинну стрічку через LLM і надсилає відібрані новини в Telegram.

## Як працює

```
RSS → fetch.py → classify.py ──[LLM]──► відфільтровані новини
                      │                        │
               classification_cache.json    app.py (веб)
                                            broadcaster.py (Telegram, кожні 15 хв)
```

## Структура

```
agent/
  config.py       — налаштування: провайдер, моделі, шляхи до файлів
  fetch.py        — завантаження RSS → список dict
  moods.py        — правила фільтрації (plain text для LLM)
  providers.py    — call_llm(prompt) → str  (Claude або Groq)
  classify.py     — оркестрація: fetch + cache + LLM → результат
  history.py      — лог класифікованих новин для /digest
  __init__.py     — публічне API пакету

app.py            — Flask UI + Telegram webhook
broadcaster.py    — Telegram push (запускається таймером)
telegram_bot.py   — тонка обгортка Telegram Bot API
cli.py            — запуск з терміналу
deploy.sh         — деплой на Oracle Cloud VM
```

## Три файли стану (тільки на сервері)

| Файл | Що зберігає |
|---|---|
| `classification_cache.json` | вердикт LLM per (посилання, mood) — не питати двічі |
| `sent_state.json` | які посилання вже надіслані в Telegram per mood |
| `history.json` | новини за останній тиждень для /digest |

## Запуск локально

```bash
python3 -m venv venv && venv/bin/pip install -r requirements.txt

# термінал:
venv/bin/python cli.py positive --verbose

# браузер:
venv/bin/python app.py   # → http://localhost:8600
```

Ключі: `ANTHROPIC_API_KEY` або `../key.txt`; `GROQ_API_KEY` або `groq_key.txt`.

## Деплой

```bash
./deploy.sh                    # Claude
LLM_PROVIDER=groq ./deploy.sh  # Groq
```

Rsync коду на `92.5.42.35`, venv, два systemd unit:
- `news-agent.service` — Flask на `127.0.0.1:8600`
- `news-agent-broadcast.timer` — broadcaster кожні 15 хв

Доступ: **https://cozy-planner.duckdns.org/news-agent/**

Логи: `sudo journalctl -u news-agent -f`

## Зміна провайдера

```python
# agent/config.py
DEFAULT_PROVIDER = "groq"   # або "anthropic"
```

Або без зміни коду: `LLM_PROVIDER=groq venv/bin/python cli.py positive`

На сервері: `deploy.sh` передає `LLM_PROVIDER` через `Environment=` в systemd — `config.py` ігнорується.
