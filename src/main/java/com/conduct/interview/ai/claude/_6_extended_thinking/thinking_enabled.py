"""
Topic: Extended thinking — budget_tokens, thinking blocks in the response
Cert notes section: Extended thinking — "Effort — how deep to think"
Run: ../venv/bin/python thinking_enabled.py

Note: extended thinking is supported on claude-sonnet-4-6 and newer.
budget_tokens controls how many tokens Claude can spend on internal reasoning.
max_tokens must be > budget_tokens to leave room for the visible output.
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

HARD_PROBLEM = (
    "A farmer has 17 sheep. All but 9 die. How many sheep does the farmer have? "
    "Explain your reasoning carefully."
)

print("=== With extended thinking (budget_tokens=3000) ===")
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

print("""
KEY TAKEAWAYS (cert notes):
- Extended thinking = Claude's internal scratchpad before answering
- budget_tokens controls reasoning depth (effort)
- max_tokens must be larger than budget_tokens
- Thinking blocks are visible to you but are NOT sent back in conversation history
- Use for: math, logic, ambiguous requirements, multi-step planning
- Don't use for: simple lookups, short answers — adds latency and cost
""")
