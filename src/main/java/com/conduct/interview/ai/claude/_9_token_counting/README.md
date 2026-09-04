# _9 — Token Counting

`client.messages.count_tokens()` — dry run, no actual request sent, no cost. Returns how many input tokens the call would use.

Use it as a guard before sending: if too large → truncate/summarize/reject.

```python
result = client.messages.count_tokens(
    model="claude-haiku-4-5-20251001",
    system="...",
    tools=[...],
    messages=[...],
)
result.input_tokens   # everything counts: system + tools + history

if result.input_tokens > MAX:
    raise ValueError(f"Too large: {result.input_tokens}")
```

Approximate input cost per 1M tokens: Haiku $0.80 · Sonnet $3.00 · Opus $15.00

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: `client`, `PRICES`, `estimate_cost` — not runnable |
| `count_tokens_simple.py` | Count tokens for a simple message |
| `system_prompt_cost.py` | System prompt adds tokens |
| `tools_cost.py` | Tool schemas add tokens |
| `token_growth_over_turns.py` | Token growth over conversation turns |
| `cost_across_models.py` | Same request — cost across models |
| `token_guard_pattern.py` | Block request if too large |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _9_token_counting && ../venv/bin/python count_tokens_simple.py
```
