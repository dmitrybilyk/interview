# _7 — Extended Thinking

Claude thinks internally before answering — spends tokens on a private scratchpad, then gives the visible answer. Costs more, worth it for hard logic/math/planning.

**Tool loop rule:** if Claude emits a `thinking` block alongside a `tool_use` block, send the entire `response.content` back unchanged in the next request. The API verifies a signature on it. Strip it → broken chain.  
**Regular turns:** don't pass thinking blocks back — they're ephemeral, not conversation history.

**Old API (Sonnet 4.6 only):**
```python
thinking={"type": "enabled", "budget_tokens": 3000}  # hard ceiling you pick
```

**New API (Sonnet 5, Opus 5, Fable 5 — budget_tokens → 400):**
```python
thinking={"type": "adaptive"},        # Claude decides how much to think
output_config={"effort": "high"},     # low | medium | high | xhigh | max
```

On Sonnet 5 with `adaptive`, `block.thinking` may be empty — thinking still ran (check `usage.output_tokens_details.thinking_tokens`). The block carries encrypted state; pass the object back, not the text.

**Pitfall:** on Opus 5, `thinking={"type": "disabled"}` with tools can cause tool calls to appear as plain text (silently never executes). Use `adaptive` + `effort: low` instead.

## Scripts
| File | Demonstrates |
|---|---|
| `_1_thinking_disabled_baseline.py` | No thinking — baseline (Sonnet 4.6) |
| `_2_thinking_enabled.py` | Old API: `budget_tokens` (Sonnet 4.6) |
| `_1_1_thinking_sonnet5_adaptive.py` | New API: `adaptive` + `effort`, loops low/medium/high |
| `_2_2_thinking_sonnet5.py` | New API: single call, harder problem to trigger thinking block |
| `_3_thinking_logic_puzzle.py` | Logic puzzle with visible thought process |
| `_4_thinking_multi_step_tool_use.py` | Thinking interleaved with tool_use loop — why you echo `response.content` back unchanged |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _7_extended_thinking && ../venv/bin/python _2_thinking_enabled.py
```
