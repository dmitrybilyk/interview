"""
Topic: Basic API call — models, tokens, temperature, context window
Cert notes section: MFO Foundations
Run: ../venv/bin/python _2_temperature_effect.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("\n=== Temperature comparison ===")
prompt = "Give me 3 distinct high-level approaches to refactor a massive 500-line function."

for temp in [0.0, 1.0]:
    r = client.messages.create(
        model="claude-haiku-4-5-20251001",
        max_tokens=1500,
        extra_body={"temperature": temp},
        messages=[{"role": "user", "content": prompt}],
    )
    print(f"\n--- temp={temp} ---")
    print(r.content[0].text.strip())
