"""
Topic: Extended thinking on a logic / multi-step reasoning problem
Cert notes section: Extended thinking — "Effort — how deep to think"
Run: ../venv/bin/python _3_thinking_logic_puzzle.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

puzzle = (
    "Three boxes are labelled 'Apples', 'Oranges', and 'Apples and Oranges'. "
    "All labels are wrong. You pick one fruit from the 'Apples and Oranges' box "
    "and it is an apple. What is in each box? Show your reasoning."
)

print("=== Logic puzzle with thinking ===")
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
