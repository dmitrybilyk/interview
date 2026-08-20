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

## What the script demonstrates
1. Count tokens for a simple message
2. System prompt adds tokens
3. Tools add tokens (schema overhead)
4. Token growth over conversation turns
5. Same request — cost comparison across models
6. Guard pattern: block request if too large

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _8_token_counting && python script.py
```
