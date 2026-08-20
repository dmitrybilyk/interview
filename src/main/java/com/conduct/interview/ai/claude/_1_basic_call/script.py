"""
Topic: Basic API call — models, tokens, temperature, context window
Cert notes section: MFO Foundations
Run: ../venv/bin/python script.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

QUESTION = "In one sentence, what is the capital of France?"

# ── 1. Basic call — Haiku (fastest, cheapest) ─────────────────────────────
print("=== 1. Basic call with Haiku ===")
response = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    messages=[{"role": "user", "content": QUESTION}],
)
print("Answer:", response.content[0].text)
print("Stop reason:", response.stop_reason)   # end_turn | max_tokens | tool_use
print("Usage:", response.usage)               # input_tokens + output_tokens = cost

# ── 2. Same question — Sonnet (balanced) ──────────────────────────────────
print("\n=== 2. Same question with Sonnet ===")
response_sonnet = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=256,
    messages=[{"role": "user", "content": QUESTION}],
)
print("Answer:", response_sonnet.content[0].text)
print("Usage:", response_sonnet.usage)

# ── 3. Temperature — low (deterministic-ish) vs high (creative) ───────────
print("\n=== 3. Temperature effect ===")
creative_q = "Give me one unusual name for a coffee shop."

for temp in [0.0, 1.0]:
    r = client.messages.create(
        model="claude-haiku-4-5-20251001",
        max_tokens=64,
        temperature=temp,
        messages=[{"role": "user", "content": creative_q}],
    )
    print(f"  temp={temp}: {r.content[0].text.strip()}")

# ── 4. max_tokens — stop_reason becomes 'max_tokens' when cut short ───────
print("\n=== 4. max_tokens cutoff ===")
r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=5,       # intentionally tiny
    messages=[{"role": "user", "content": "Write a 3-sentence story about a robot."}],
)
print("Text:", r.content[0].text)
print("Stop reason:", r.stop_reason)   # max_tokens — response was cut

# ── 5. Context window — input_tokens grows with prompt size ───────────────
print("\n=== 5. Token usage grows with context ===")
short_r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=32,
    messages=[{"role": "user", "content": "Hi"}],
)
long_r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=32,
    messages=[{"role": "user", "content": "Hi " * 500}],  # 500-word prompt
)
print(f"Short prompt input tokens: {short_r.usage.input_tokens}")
print(f"Long  prompt input tokens: {long_r.usage.input_tokens}")
print("Context window for Haiku is 200k tokens — input + output must stay under that.")
