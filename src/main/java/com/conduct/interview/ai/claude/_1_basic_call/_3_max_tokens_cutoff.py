"""
Topic: max_tokens — stop_reason becomes 'max_tokens' when the response is cut short
Cert notes section: MFO Foundations
Run: ../venv/bin/python _3_max_tokens_cutoff.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== max_tokens cutoff ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=5,       # intentionally tiny
    messages=[{"role": "user", "content": "Write a 3-sentence story about a robot."}],
)
print("Text:", r.content[0].text)
print("Stop reason:", r.stop_reason)   # max_tokens — response was cut
