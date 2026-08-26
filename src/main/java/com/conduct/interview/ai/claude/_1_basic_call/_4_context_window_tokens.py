"""
Topic: Context window — input_tokens grows with prompt size
Cert notes section: MFO Foundations
Run: ../venv/bin/python _4_context_window_tokens.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Token usage grows with context ===")
short_r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=32,
    messages=[{"role": "user", "content": "Hi"}],
)
long_r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=32,
    messages=[{"role": "user", "content": "Hi " * 500}],  # 500-word prompt
)
print(f"Short prompt input tokens: {short_r.usage.input_tokens}")
print(f"Long  prompt input tokens: {long_r.usage.input_tokens}")
print("Context window for Haiku is 200k tokens — input + output must stay under that.")
