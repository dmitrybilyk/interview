# _7 — Extended Thinking

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

## Scripts
| File | Demonstrates |
|---|---|
| `thinking_disabled_baseline.py` | Same problem without thinking (may get it wrong) |
| `thinking_enabled.py` | Same problem with thinking (shows reasoning process) |
| `thinking_logic_puzzle.py` | A classic logic puzzle with visible thought process |
| `_4_thinking_multi_step_tool_use.py` | Multi-step: thinking interleaved with `tool_use` across a loop, and why you must echo `response.content` back unchanged mid-loop |

## ⚠️ API drift: `budget_tokens` vs. `adaptive` + `effort` (current API)

`_2_`/`_3_` above use `thinking: {"type": "enabled", "budget_tokens": N}` — a hard
token ceiling **you** pick by hand. That's what a lot of cert material still shows,
and it still works on `claude-sonnet-4-6`, but only as a **deprecated transitional
escape hatch**. On current models (`claude-opus-5`, `claude-sonnet-5`,
`claude-fable-5`, `claude-opus-4-6/4-7/4-8`) `budget_tokens` is **rejected with a
400** — there is no manual token budget anymore.

The replacement:

```python
response = client.messages.create(
    model="claude-opus-5",
    max_tokens=16000,
    thinking={"type": "adaptive"},        # Claude decides how much to think, dynamically
    output_config={"effort": "high"},     # the actual "level" knob: low | medium | high | xhigh | max
    messages=[{"role": "user", "content": "..."}],
)
```

- `adaptive` = Claude itself decides, per-request, how much internal reasoning it needs — you no longer guess a token number.
- `effort` is the real "level" control now: it steers overall depth/cost, not a raw token cap.
- On Claude Opus 5 specifically, thinking is **on by default** — omitting `thinking` already runs adaptive.

See `_4_thinking_multi_step_tool_use.py` for `adaptive` combined with a multi-step `tool_use` loop.

### All `thinking.type` values

`adaptive` isn't the only option — there are three `type`s total, and which ones a
model accepts varies:

| `type` | Meaning | Where it's valid |
|---|---|---|
| `"adaptive"` | Claude decides dynamically how much to think; steer depth with `output_config.effort`. **Recommended on every current model.** | All current models. Omitting `thinking` entirely is equivalent to `adaptive` on Claude Opus 5, Sonnet 5, Fable 5 — but on Opus 4.7/4.8 omitting it means **no thinking**, so you must set it explicitly there. |
| `"disabled"` | Thinking fully off — no reasoning, no `thinking` blocks, cheaper/faster for tasks that don't need it. | Opus 4.6/4.7/4.8, Sonnet 4.6/5. On Claude Opus 5 it's accepted **only at `effort: "high"` or below** — pairing `"disabled"` with `xhigh`/`max` is a 400. **Rejected outright on Claude Fable 5** (thinking is always on there; omit the param instead of trying to disable it). |
| `"enabled"` + `budget_tokens: N` | The old fixed-ceiling mode — you pick a hard token cap by hand (min 1024, must be `< max_tokens`). | Only pre-4.6 models (Sonnet 4.5, Haiku 4.5, ...) need this. Still functional as a deprecated escape hatch on Opus 4.6/Sonnet 4.6. **400 on Fable 5, Opus 5, Opus 4.7/4.8, Sonnet 5.** |

Pitfall worth knowing for interviews: on Claude Opus 5, explicitly disabling thinking
(rather than just lowering `effort`) can make the model write a tool call into its
*visible text* instead of a proper `tool_use` block — the turn "succeeds" but the
call silently never runs. Prefer `adaptive` + low/medium `effort` over `"disabled"`
when you just want cheaper/faster, not zero reasoning.

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _7_extended_thinking && python thinking_disabled_baseline.py   # or any other file in this directory
```
