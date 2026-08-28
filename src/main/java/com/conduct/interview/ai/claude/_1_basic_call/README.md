# _1 — Basic API Call

`client.messages.create(model, max_tokens, messages)` → `response.content[0].text`

`stop_reason` — why generation stopped: `end_turn` | `max_tokens` | `tool_use`  
`usage.input_tokens + output_tokens` — what you pay for  
`max_tokens` — hard cap on output; model stops mid-sentence if hit  
Temperature: 0 = focused/deterministic, 1 = creative/random

```python
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    temperature=0.0,
    messages=[{"role": "user", "content": "..."}],
)
r.content[0].text
r.stop_reason
r.usage
```

## Scripts
| File | Demonstrates |
|---|---|
| `basic_call.py` | Haiku vs Sonnet — latency and cost |
| `temperature_effect.py` | Temperature 0.0 vs 1.0 |
| `max_tokens_cutoff.py` | `stop_reason: max_tokens` when cut short |
| `context_window_tokens.py` | Token count grows with prompt size |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _1_basic_call && ../venv/bin/python basic_call.py
```
