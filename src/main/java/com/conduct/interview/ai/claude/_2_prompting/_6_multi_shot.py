"""
Topic: Multi-shot — more examples than few-shot to lock format/vocabulary tighter
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python _6_multi_shot.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Multi-shot (5 examples) ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=16,
    messages=[
        {"role": "user",      "content": "Classify: 'Best purchase I ever made!'"},
        {"role": "assistant", "content": "pos"},
        {"role": "user",      "content": "Classify: 'Broken on arrival, terrible.'"},
        {"role": "assistant", "content": "neg"},
        {"role": "user",      "content": "Classify: 'Arrived on time, nothing special.'"},
        {"role": "assistant", "content": "neu"},
        {"role": "user",      "content": "Classify: 'Absolutely love it, exceeded expectations!'"},
        {"role": "assistant", "content": "pos"},
        {"role": "user",      "content": "Classify: 'Would not recommend to anyone.'"},
        {"role": "assistant", "content": "neg"},
        {"role": "user",      "content": "Classify: 'It works as described.'"},
    ],
)
print("Multi-shot result:", r.content[0].text.strip())
