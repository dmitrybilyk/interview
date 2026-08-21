# _8 — Token Counting

**Cert notes section:** "2 API features reduce what you pay" + "What affects budget"

## What this covers
| What affects token count | Detail |
|---|---|
| Model selection | Haiku ~19x cheaper input than Opus |
| Prompt + context size | System prompt, tools, history all count |
| Number of tool calls | Each round trip = more input tokens |
| Stream vs batch | Same token cost, different latency trade-off |

## Key API
```python
# Dry run — counts tokens WITHOUT sending the actual request
result = client.messages.count_tokens(
    model="claude-haiku-4-5-20251001",
    system="...",       # optional
    tools=[...],        # optional — tools add overhead
    messages=[...],
)
print(result.input_tokens)
```

## Guard pattern
```python
MAX_TOKENS = 5000
check = client.messages.count_tokens(model=model, messages=messages)
if check.input_tokens > MAX_TOKENS:
    # truncate / summarize / reject before spending money
    raise ValueError(f"Too large: {check.input_tokens} tokens")
```

## Approximate pricing per 1M tokens
| Model | Input | Output |
|---|---|---|
| Haiku 4.5 | $0.80 | $4.00 |
| Sonnet 4.6 | $3.00 | $15.00 |
| Opus 4.7 | $15.00 | $75.00 |

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: `client`, `PRICES`, `estimate_cost` — not runnable on its own |
| `count_tokens_simple.py` | Count tokens for a simple message |
| `system_prompt_cost.py` | System prompt adds tokens |
| `tools_cost.py` | Tools add tokens (schema overhead) |
| `token_growth_over_turns.py` | Token growth over conversation turns |
| `cost_across_models.py` | Same request — cost comparison across models |
| `token_guard_pattern.py` | Guard pattern: block request if too large |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _8_token_counting && python count_tokens_simple.py   # or any other file in this directory
```
