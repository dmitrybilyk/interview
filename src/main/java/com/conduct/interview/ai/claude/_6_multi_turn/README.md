# _6 — Multi-Turn Conversation

Claude is stateless. Full history is sent on every call. Tokens grow every turn — you pay for all of it each time.

```python
messages = []
messages.append({"role": "user", "content": "Hello"})
r = client.messages.create(model=..., messages=messages)
messages.append({"role": "assistant", "content": r.content[0].text})
# next turn: full messages list sent again
```

**When context gets too big, three options:**

| Strategy | How | Downside |
|---|---|---|
| Prune | `messages = messages[-N:]` | Loses early context |
| Compact | Summarize history into one message, restart | Loses detail |
| Clear | Fresh session | Loses everything |

System prompt is the cheapest way to persist instructions — constant cost, not in `messages[]`.

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: `client`, `count_tokens`, `chat` — not runnable |
| `multi_turn_basics.py` | Token growth over turns |
| `pruning_strategy.py` | Keep last N messages |
| `compacting_strategy.py` | Summarize + restart |
| `clearing_strategy.py` | Fresh session |
| `system_prompt_persists.py` | System prompt as constant-cost alternative |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _6_multi_turn && ../venv/bin/python multi_turn_basics.py
```
