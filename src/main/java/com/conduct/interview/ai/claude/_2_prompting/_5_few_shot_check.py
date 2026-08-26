"""
Topic: Few-shot — show Claude exactly the format you want via example turns
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python _5_few_shot_check.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Few-shot (2 examples) ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=32,
    messages=[
        {"role": "user",    "content": "Classify: 'Today I'm reading about multithreading'"},
        {"role": "assistant","content": "BEST!"},
        {"role": "user",    "content": "Classify: 'I'll have a rest all the day because it's my day off)'"},
        {"role": "assistant","content": "MORE_STUDYING_NEEDED!"},
        {"role": "user",    "content": "Classify: 'I like talking to Claud discussing some learning topics'"},
    ],
)
print("Few-shot result:", r.content[0].text.strip())
