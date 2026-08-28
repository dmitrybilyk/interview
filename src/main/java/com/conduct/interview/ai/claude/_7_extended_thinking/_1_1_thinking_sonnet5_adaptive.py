"""
Topic: Sonnet 5 — adaptive thinking + effort (new API, no budget_tokens)
Cert notes section: Extended thinking — "Effort — how deep to think"
Run: ../venv/bin/python _1_1_thinking_sonnet5_adaptive.py

On current models (Sonnet 5, Opus 5, Fable 5) budget_tokens is rejected with 400.
The new API uses:
  thinking={"type": "adaptive"}      — Claude decides per-request how much to think
  output_config={"effort": "..."}    — steers depth: low | medium | high | xhigh | max

Omitting `thinking` entirely on Sonnet 5 is equivalent to {"type": "adaptive"}.
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

HARD_PROBLEM = (
    "A farmer has 17 sheep. All but 9 die. How many sheep does the farmer have? "
    "Explain your reasoning carefully."
)

for effort in ("low", "medium", "high"):
    print(f"\n{'='*60}")
    print(f"=== Sonnet 5 — adaptive thinking, effort={effort!r} ===")
    print(f"{'='*60}")

    r = client.messages.create(
        model="claude-sonnet-5",
        max_tokens=8000,
        thinking={"type": "adaptive"},       # Claude decides how much reasoning it needs
        output_config={"effort": effort},    # steers depth/cost, not a hard token cap
        messages=[{"role": "user", "content": HARD_PROBLEM}],
    )

    thinking_chars = 0
    for block in r.content:
        if block.type == "thinking":
            thinking_chars = len(block.thinking)
            print(f"\n[THINKING — {thinking_chars} chars]")
            preview = block.thinking[:300]
            print(preview + ("..." if thinking_chars > 300 else ""))
        elif block.type == "text":
            print(f"\n[ANSWER]\n{block.text.strip()}")

    print(f"\nTokens: input={r.usage.input_tokens}, output={r.usage.output_tokens}")

print("""
KEY TAKEAWAYS:
- adaptive = Claude itself decides how many tokens to spend on reasoning, per request
- effort   = the real "level" knob (low/medium/high/xhigh/max) — steers depth and cost
- budget_tokens is gone on current models — 400 if you use it
- On Sonnet 5, omitting thinking= entirely behaves like {"type": "adaptive"}
- Pitfall: {"type": "disabled"} on Opus 5 with tools can cause tool calls to appear as
  plain text in the response (silently never executes). Use adaptive+low instead.
""")
