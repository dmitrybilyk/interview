"""
Topic: Output constraint — force one of a fixed set of values
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python _2_output_constraint.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Output constraint ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=8,
    messages=[{
        "role": "user",
        "content": (
            "Classify the review sentiment. "
            "Reply with exactly one word: pos, neg, or neutral.\n\n"
            "Review: 'The pizza was okay but the service was terrible. worst experience ever.'"
        )
    }],
)
print("Constrained output:", r.content[0].text.strip())
