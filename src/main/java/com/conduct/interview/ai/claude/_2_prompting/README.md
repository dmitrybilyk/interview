# _2 — Prompting Techniques

The prompt is the program. Format it right and you control the output shape.

**Few-shot** — put examples in `messages[]` as user/assistant pairs; Claude continues the pattern.  
**System prompt** — rules applied to every turn; constant cost, not in `messages[]`.  
**Output constraint** — "Reply with exactly one word: pos, neg, or neutral." Forces the format.  
**XML tags** — unambiguous structure when prompt has multiple parts: `<task>`, `<rules>`, `<input>`.

```python
# Few-shot
messages=[
    {"role": "user",      "content": "Input 1"},
    {"role": "assistant", "content": "Expected output 1"},
    {"role": "user",      "content": "New input"},
]

# System prompt
client.messages.create(system="You are a JSON-only API. Never write prose.", messages=[...])
```

## Scripts
| File | Demonstrates |
|---|---|
| `zero_shot.py` | Zero-shot classification |
| `output_constraint.py` | Single-word forced output |
| `system_prompt.py` | System prompt enforcing JSON-only |
| `few_shot.py` | 2-example few-shot |
| `xml_structure.py` | XML tags with edge case handling |
| `structured_json_output.py` | Full JSON extraction with `json.loads()` |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _2_prompting && ../venv/bin/python zero_shot.py
```
