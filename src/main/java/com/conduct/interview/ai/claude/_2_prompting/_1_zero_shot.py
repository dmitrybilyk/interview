"""
Topic: Zero-shot prompting — no examples, no output constraint
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python _1_zero_shot.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Zero-shot ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    messages=[{"role": "user", "content": "Is this review positive or negative? 'The pizza was okay but the service was slow.'"}],
)
print(r.content[0].text.strip())
