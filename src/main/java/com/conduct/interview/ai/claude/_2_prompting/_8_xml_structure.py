"""
Topic: XML tags — structure complex prompts for clarity
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python _8_xml_structure.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== XML tags for structure ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    messages=[{
        "role": "user",
        "content": """
<task>Classify the customer review sentiment</task>
<rules>
  Reply with exactly one word: pos, neg, or neutral.
  If the review is empty, reply: unknown.
</rules>
<review>Perfect</review>
"""
    }],
)
print("Edge case (empty review):", r.content[0].text.strip())
