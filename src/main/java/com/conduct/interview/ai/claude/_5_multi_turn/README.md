# _5 — Multi-Turn Conversation & Context Budget

**Cert notes section:** Model selection and keeping multi-turn session in budget

## What this covers
| Strategy | When to use |
|---|---|
| Pruning | Drop old messages when context grows too large |
| Compacting | Summarize history into a condensed message |
| Clearing | Fresh session — when history is no longer needed |
| Sub-agent handoff | Delegate subtask to isolated agent with smaller context |

## How multi-turn works
```python
messages = []

# Turn 1
messages.append({"role": "user", "content": "Hello"})
response = client.messages.create(model=..., messages=messages)
messages.append({"role": "assistant", "content": response.content[0].text})

# Turn 2 — history is sent again (Claude is stateless)
messages.append({"role": "user", "content": "What did I say?"})
response = client.messages.create(model=..., messages=messages)
# tokens grow every turn
```

## Token growth
- Every turn sends the **full history** again (Claude has no server-side memory)
- `input_tokens` grows linearly with conversation length
- You pay for the full context every single call

## Pruning pattern
```python
MAX = 4  # keep last 2 exchanges
messages = messages[-MAX:]
```

## Compacting pattern
```python
summary_response = client.messages.create(
    messages=messages + [{"role": "user", "content": "Summarize our conversation."}]
)
messages = [
    {"role": "user",      "content": f"[Context] {summary}"},
    {"role": "assistant", "content": "Understood."}
]
```

## System prompt vs history
- System prompt: constant per-call cost, not in `messages[]`, cheapest way to persist instructions
- History: grows every turn, paid every call

## What the script demonstrates
1. Token growth over conversation turns
2. Pruning (keep last N messages)
3. Compacting (summarize + restart)
4. Clearing (fresh session, memory lost)
5. System prompt as a constant-cost alternative

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _5_multi_turn && python script.py
```
