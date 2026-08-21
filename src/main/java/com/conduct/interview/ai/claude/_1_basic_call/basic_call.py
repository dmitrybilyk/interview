"""
Topic: Basic API call — model comparison (Haiku vs Sonnet)
Cert notes section: MFO Foundations
Run: ../venv/bin/python basic_call.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

QUESTION = "In one sentence, what is the capital of France?"

# ── Basic call — Haiku (fastest, cheapest) ────────────────────────────────
print("=== Basic call with Haiku ===")
response = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    messages=[{"role": "user", "content": QUESTION}],
)
print("Answer:", response.content[0].text)
print("Stop reason:", response.stop_reason)   # end_turn | max_tokens | tool_use
print("Usage:", response.usage)               # input_tokens + output_tokens = cost

# ── Same question — Sonnet (balanced) ──────────────────────────────────────
print("\n=== Same question with Sonnet ===")
response_sonnet = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=256,
    messages=[{"role": "user", "content": QUESTION}],
)
print("Answer:", response_sonnet.content[0].text)
print("Usage:", response_sonnet.usage)
