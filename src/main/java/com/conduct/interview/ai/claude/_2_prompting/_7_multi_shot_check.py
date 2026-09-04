"""
Topic: Multi-shot — more examples than few-shot to lock format/vocabulary tighter
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python _7_multi_shot_check.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Multi-shot: vocabulary is locked by many examples ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=16,
    messages=[
        {"role": "user",      "content": "Classify: 'Today I'm reading about multithreading'"},
        {"role": "assistant", "content": "BEST!"},
        {"role": "user",      "content": "Classify: 'I'll have a rest all day because it's my day off'"},
        {"role": "assistant", "content": "GOOD_ENOUGH!"},
        {"role": "user",      "content": "Classify: 'Sometimes I study, sometimes I lazy'"},
        {"role": "assistant", "content": "GOOD_ENOUGH!"},
        {"role": "user",      "content": "Classify: 'Spent the morning watching TV'"},
        {"role": "assistant", "content": "MORE_STUDYING_NEEDED!"},
        {"role": "user",      "content": "Classify: 'Just finished a coding exercise'"},
        {"role": "assistant", "content": "BEST!"},
        {"role": "user",      "content": "Classify: 'Yesterday I did a lot of studying, I\'ll have a day off today'"},
    ],
)
result = r.content[0].text.strip()
print("Multi-shot result:", result)

allowed = {"BEST!", "MORE_STUDYING_NEEDED!", "GOOD_ENOUGH!"}
assert result in allowed, f"Unexpected label: {result}"
print("Vocabulary locked correctly — only allowed labels produced.")
