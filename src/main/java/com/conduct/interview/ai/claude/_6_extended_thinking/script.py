"""
Topic: Extended thinking — budget_tokens, thinking blocks, when to use
Cert notes section: Extended thinking — "Effort — how deep to think"
Run: ../venv/bin/python script.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Extended thinking is supported on claude-sonnet-4-6 and newer.
# budget_tokens controls how many tokens Claude can spend on internal reasoning.
# max_tokens must be > budget_tokens to leave room for the visible output.

HARD_PROBLEM = (
    "A farmer has 17 sheep. All but 9 die. How many sheep does the farmer have? "
    "Explain your reasoning carefully."
)

# ── 1. Without thinking — fast but may miss nuance ────────────────────────
print("=== 1. Without extended thinking ===")
r_normal = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=512,
    messages=[{"role": "user", "content": HARD_PROBLEM}],
)
print(r_normal.content[0].text.strip())
print(f"\nTokens used: {r_normal.usage}")

# ── 2. With extended thinking — Claude reasons before answering ────────────
print("\n=== 2. With extended thinking (budget_tokens=3000) ===")
r_thinking = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=8000,
    thinking={
        "type": "enabled",
        "budget_tokens": 3000    # Claude can spend up to 3000 tokens reasoning internally
    },
    messages=[{"role": "user", "content": HARD_PROBLEM}],
)

# Response contains both 'thinking' blocks and 'text' blocks
for block in r_thinking.content:
    if block.type == "thinking":
        print(f"\n[THINKING BLOCK — internal reasoning, {len(block.thinking)} chars]")
        print(block.thinking[:400] + "..." if len(block.thinking) > 400 else block.thinking)
    elif block.type == "text":
        print(f"\n[ANSWER]\n{block.text.strip()}")

print(f"\nTokens used: {r_thinking.usage}")

# ── 3. Thinking on a logic / multi-step problem ───────────────────────────
print("\n=== 3. Logic puzzle with thinking ===")
puzzle = (
    "Three boxes are labelled 'Apples', 'Oranges', and 'Apples and Oranges'. "
    "All labels are wrong. You pick one fruit from the 'Apples and Oranges' box "
    "and it is an apple. What is in each box? Show your reasoning."
)
r_puzzle = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=6000,
    thinking={"type": "enabled", "budget_tokens": 2000},
    messages=[{"role": "user", "content": puzzle}],
)
for block in r_puzzle.content:
    if block.type == "thinking":
        print(f"\n[THINKING — {len(block.thinking)} chars — first 300]")
        print(block.thinking[:300])
    elif block.type == "text":
        print(f"\n[ANSWER]\n{block.text.strip()}")

# ── Key notes ─────────────────────────────────────────────────────────────
print("""
KEY TAKEAWAYS (cert notes):
- Extended thinking = Claude's internal scratchpad before answering
- budget_tokens controls reasoning depth (effort)
- max_tokens must be larger than budget_tokens
- Thinking blocks are visible to you but are NOT sent back in conversation history
- Use for: math, logic, ambiguous requirements, multi-step planning
- Don't use for: simple lookups, short answers — adds latency and cost
""")
