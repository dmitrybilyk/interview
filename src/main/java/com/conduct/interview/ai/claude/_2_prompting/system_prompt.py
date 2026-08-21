"""
Topic: System prompt — rules that apply to every response
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python system_prompt.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== System prompt ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    system="You are a strict JSON-only API. Never write prose. Always respond with valid JSON.",
    messages=[{"role": "user", "content": "What is the capital of Germany?"}],
)
print(r.content[0].text.strip())
