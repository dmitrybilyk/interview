"""
Topic: Structured JSON output — system prompt constraint + json.loads parsing
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python _10_structured_json_output_check.py
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
            'Extract data from this answer as JSON with keys: '
            '"Correctness" (high/average/low), "topic" (string), "score" (1-5 int).\n\n'
            'Answer: "The role requires a senior Java developer with banking domain experience. '
            'I have 15 years of Java development, the last 8 in core banking systems at two major banks, '
            'delivering high-throughput transaction processing and regulatory compliance features."'         )
    }],
)
raw = r.content[0].text.strip()
print("Raw:", raw)
# model sometimes wraps in ```json ... ``` despite the instruction
if raw.startswith("```"):
    raw = raw.split("```")[1]
    if raw.startswith("json"):
        raw = raw[4:]
    raw = raw.strip()
parsed = json.loads(raw)
print("Parsed:", parsed)
