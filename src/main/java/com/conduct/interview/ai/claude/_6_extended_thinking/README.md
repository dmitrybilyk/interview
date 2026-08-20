# _6 — Extended Thinking

**Cert notes section:** Extended thinking — "Effort — how deep to think"

## What this covers
| Concept | Detail |
|---|---|
| `budget_tokens` | Max tokens Claude can spend reasoning (effort level) |
| Thinking blocks | Internal scratchpad — visible in response but NOT sent back in history |
| When to use | Math, logic puzzles, ambiguous requirements, multi-step planning |
| When NOT to use | Simple lookups, one-sentence answers — adds cost and latency |

## Key API
```python
response = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=8000,               # must be > budget_tokens
    thinking={
        "type": "enabled",
        "budget_tokens": 3000      # controls reasoning depth
    },
    messages=[{"role": "user", "content": "..."}],
)

for block in response.content:
    if block.type == "thinking":
        print(block.thinking)     # internal reasoning
    elif block.type == "text":
        print(block.text)         # final answer
```

## Important rules
- `max_tokens` must exceed `budget_tokens` (leave room for the answer)
- Thinking blocks are NOT passed back in conversation history
- Supported on `claude-sonnet-4-6` and newer models
- Temperature is locked to 1.0 when thinking is enabled

## When thinking helps
- The sheep puzzle: "17 sheep, all but 9 die" — sounds like subtraction but the answer is 9
- Logic box problems
- Multi-step planning where intermediate steps matter
- Ambiguous specifications where trade-offs need to be weighed

## What the script demonstrates
1. Same problem without thinking (may get it wrong)
2. Same problem with thinking (shows reasoning process)
3. A classic logic puzzle with visible thought process

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _6_extended_thinking && python script.py
```
