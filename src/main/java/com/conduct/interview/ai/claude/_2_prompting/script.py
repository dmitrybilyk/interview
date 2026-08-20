"""
Topic: System prompt, few-shot, output constraints, XML tags, JSON output
Cert notes section: 4 Techniques That Give Claude a Reliable Output Shape
Run: ../venv/bin/python script.py
"""

import os, json
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# ── 1. Zero-shot — no examples, no constraint ─────────────────────────────
print("=== 1. Zero-shot ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    messages=[{"role": "user", "content": "Is this review positive or negative? 'The pizza was okay but the service was slow.'"}],
)
print(r.content[0].text.strip())

# ── 2. Output constraint — force one of a fixed set of values ─────────────
print("\n=== 2. Output constraint ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=8,
    messages=[{
        "role": "user",
        "content": (
            "Classify the review sentiment. "
            "Reply with exactly one word: pos, neg, or neutral.\n\n"
            "Review: 'The pizza was okay but the service was slow.'"
        )
    }],
)
print("Constrained output:", r.content[0].text.strip())

# ── 3. System prompt — rules for every response ───────────────────────────
print("\n=== 3. System prompt ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    system="You are a strict JSON-only API. Never write prose. Always respond with valid JSON.",
    messages=[{"role": "user", "content": "What is the capital of Germany?"}],
)
print(r.content[0].text.strip())

# ── 4. Few-shot — show Claude exactly the format you want ─────────────────
print("\n=== 4. Few-shot (2 examples) ===")
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

# ── 5. XML tags — structure complex prompts for clarity ───────────────────
print("\n=== 5. XML tags for structure ===")
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
<review></review>
"""
    }],
)
print("Edge case (empty review):", r.content[0].text.strip())

# ── 6. Structured JSON output ─────────────────────────────────────────────
print("\n=== 6. Structured JSON output ===")
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
