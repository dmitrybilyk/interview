"""
Topic: Few-shot — show Claude exactly the format you want via example turns
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python few_shot.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Few-shot (2 examples) ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=32,
    messages=[
        {"role": "user",    "content": "Classify: 'Best purchase I ever made!'"},
        {"role": "assistant","content": "pos"},
        {"role": "user",    "content": "Classify: 'Broken on arrival, terrible.'"},
        {"role": "assistant","content": "neg"},
        {"role": "user",    "content": "Classify: 'Arrived on time, nothing special.'"},
    ],
)
print("Few-shot result:", r.content[0].text.strip())
