"""
Topic: Baseline — no extended thinking (fast but may miss nuance)
Cert notes section: Extended thinking — "Effort — how deep to think"
Run: ../venv/bin/python _1_thinking_disabled_baseline.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

HARD_PROBLEM = (
    "A farmer has 17 sheep. All but 9 die. How many sheep does the farmer have? "
    "Explain your reasoning carefully."
)

print("=== Without extended thinking ===")
r_normal = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=512,
    messages=[{"role": "user", "content": HARD_PROBLEM}],
)
print(r_normal.content[0].text.strip())
print(f"\nTokens used: {r_normal.usage}")
