"""
Topic: Extended thinking on Sonnet 5 — adaptive + effort (new API)
Cert notes section: Extended thinking — "Effort — how deep to think"
Run: ../venv/bin/python _2_2_thinking_sonnet5.py

Mirrors _2_thinking_enabled.py but for Sonnet 5 where budget_tokens is a 400 error.
New API: thinking={"type": "adaptive"} + output_config={"effort": "high"}
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

HARD_PROBLEM = """
Five pirates (ranked 1–5, most senior first) must divide 100 gold coins.
Rules:
- The most senior living pirate proposes a split.
- All pirates vote. If >= 50% approve, the split is accepted.
- If it fails, the proposer is thrown overboard and the next pirate proposes.
- Pirates are perfectly rational and greedy (maximise own gold), but prefer
  to stay alive, and if indifferent prefer to see others thrown overboard.

What split should Pirate 1 propose, and why will it be accepted?
Work through the logic from the simplest case (2 pirates) up to 5.
"""

print("=== With extended thinking on Sonnet 5 (adaptive, effort=high) ===")
r_thinking = client.messages.create(
    model="claude-sonnet-5",
    max_tokens=8000,
    thinking={"type": "adaptive"},      # Claude decides how much to think per request
    output_config={"effort": "high"},   # steers depth: low | medium | high | xhigh | max
    messages=[{"role": "user", "content": HARD_PROBLEM}],
)

print(f"Block types returned: {[b.type for b in r_thinking.content]}")

for block in r_thinking.content:
    if block.type == "thinking":
        print(f"\n[THINKING BLOCK — internal reasoning, {len(block.thinking)} chars]")
        print(block.thinking[:400] + "..." if len(block.thinking) > 400 else block.thinking)
    elif block.type == "text":
        print(f"\n[ANSWER]\n{block.text.strip()}")

print(f"\nTokens used: {r_thinking.usage}")

print("""
KEY TAKEAWAYS — Sonnet 5 vs Sonnet 4.6:

  Sonnet 4.6 (old API):            Sonnet 5 (new API):
  thinking={                        thinking={"type": "adaptive"}
    "type": "enabled",              output_config={"effort": "high"}
    "budget_tokens": 3000
  }

- budget_tokens = hard ceiling YOU pick (guessing game)
- effort        = level YOU pick, Claude picks actual token count dynamically
- budget_tokens on Sonnet 5 → 400 error
- On Sonnet 5, omitting thinking= entirely = {"type": "adaptive"} (on by default)
- Thinking blocks behave the same: visible in response, NOT sent back in history
""")
