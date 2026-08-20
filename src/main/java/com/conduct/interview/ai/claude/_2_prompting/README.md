# _2 — Prompting Techniques

**Cert notes section:** 4 Techniques That Give Claude a Reliable Output Shape

## What this covers
| Technique | When to use |
|---|---|
| Zero-shot | Simple tasks, no format requirement |
| One-shot / Few-shot | When you need exact output format |
| Output constraint | Force Claude into a fixed value set (pos/neg/neutral) |
| System prompt | Rules that apply to every response in the session |
| XML tags | Structure complex prompts; separate task/rules/input clearly |
| Edge case coverage | Handle empty input, missing fields, error states in the prompt |
| JSON output | Ask Claude to return structured data |

## Key patterns

### Output constraint
```python
"Reply with exactly one word: pos, neg, or neutral."
```

### System prompt (applies to every turn)
```python
client.messages.create(
    system="You are a strict JSON-only API. Never write prose.",
    messages=[...]
)
```

### Few-shot in messages array
```python
messages=[
    {"role": "user",      "content": "Input 1"},
    {"role": "assistant", "content": "Expected output 1"},
    {"role": "user",      "content": "Input 2"},
    {"role": "assistant", "content": "Expected output 2"},
    {"role": "user",      "content": "New input"},  # Claude continues the pattern
]
```

### XML tags for structure
```xml
<task>What to do</task>
<rules>Constraints</rules>
<input>The actual data</input>
```

## What the script demonstrates
1. Zero-shot classification (uncontrolled output)
2. Output constraint (single word reply)
3. System prompt enforcing JSON-only
4. 2-example few-shot
5. XML tags for edge case (empty input)
6. Full JSON extraction with `json.loads()`

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _2_prompting && python script.py
```
