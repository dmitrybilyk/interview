# _1 — Basic API Call

**Cert notes section:** MFO Foundations

## What this covers
| Concept | Cert note |
|---|---|
| Tokens | Unit of input, output, and cost |
| Context window | Total tokens model can take in one request |
| Sampling / Temperature | Lower = more probability-dependent; AI is non-deterministic |
| Models | Haiku (fastest), Sonnet (balanced), Opus (most capable) |
| stop_reason | `end_turn` \| `max_tokens` \| `tool_use` |

## Key API fields
```python
client.messages.create(
    model="claude-haiku-4-5-20251001",  # or claude-sonnet-4-6, claude-opus-4-7
    max_tokens=256,                      # hard cap on output tokens
    temperature=0.0,                     # 0.0 = deterministic-ish, 1.0 = creative
    messages=[{"role": "user", "content": "..."}],
)
response.usage          # input_tokens + output_tokens
response.stop_reason    # why generation stopped
response.content[0].text
```

## Scripts
| File | Demonstrates |
|---|---|
| `basic_call.py` | Haiku vs Sonnet — different latency and cost |
| `temperature_effect.py` | Temperature 0.0 vs 1.0 on a creative prompt |
| `max_tokens_cutoff.py` | `stop_reason: max_tokens` when response is cut short |
| `context_window_tokens.py` | Token count grows with prompt size |

## Run
```bash
cd src/main/java/com/conduct/interview/ai/claude
source venv/bin/activate
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _1_basic_call && python basic_call.py   # or any other file in this directory
```
