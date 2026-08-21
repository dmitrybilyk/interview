"""
Topic: Structured JSON output — system prompt constraint + json.loads parsing
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python structured_json_output.py
"""

import os, json
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Structured JSON output ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    system='Respond only with valid JSON. No markdown, no prose.',
    messages=[{
        "role": "user",
        "content": (
            'Extract data from this review as JSON with keys: '
            '"sentiment" (pos/neg/neutral), "topic" (string), "score" (1-5 int).\n\n'
            'Review: "The keyboard feels amazing and the build quality is top-notch. Bought it twice!"'
        )
    }],
)
raw = r.content[0].text.strip()
print("Raw:", raw)
parsed = json.loads(raw)
print("Parsed:", parsed)
