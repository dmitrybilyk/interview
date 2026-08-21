"""
Topic: Prompt caching — cache MISS on first call, cache HIT on second (byte-identical) call
Cert notes section: "2 API features reduce what you pay: Prompt caching, Token counting"
Run: ../venv/bin/python cache_hit_vs_miss.py
"""

import os, time
from anthropic import Anthropic
from _common import LARGE_SYSTEM

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Pricing reference (haiku-4-5, per million tokens, approximate):
# Normal input:       $0.80 / M
# Cache write:        $0.96 / M  (slightly more than normal to create cache)
# Cache read:         $0.08 / M  (10x cheaper — the 90% saving)

QUESTION = "What is the main trade-off between B-tree and LSM-tree indexes?"

# ── 1. First call — cache is created (cache_creation_input_tokens) ─────────
print("=== First call — cache MISS (cache being written) ===")
t0 = time.time()
r1 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    system=[{
        "type": "text",
        "text": LARGE_SYSTEM,
        "cache_control": {"type": "ephemeral"}  # mark this block for caching
    }],
    messages=[{"role": "user", "content": QUESTION}],
)
t1 = time.time()

u1 = r1.usage
print(f"  Latency: {t1-t0:.2f}s")
print(f"  input_tokens:             {u1.input_tokens}")
print(f"  cache_creation_tokens:    {getattr(u1, 'cache_creation_input_tokens', 0)}")
print(f"  cache_read_tokens:        {getattr(u1, 'cache_read_input_tokens', 0)}")
print(f"  Answer: {r1.content[0].text[:100]}...")

# ── 2. Second call — same system prompt → cache HIT ───────────────────────
print("\n=== Second call — cache HIT (cache_read_input_tokens) ===")
t2 = time.time()
r2 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    system=[{
        "type": "text",
        "text": LARGE_SYSTEM,       # MUST be byte-for-byte identical
        "cache_control": {"type": "ephemeral"}
    }],
    messages=[{"role": "user", "content": "What is MVCC and why is it useful?"}],
)
t3 = time.time()

u2 = r2.usage
print(f"  Latency: {t3-t2:.2f}s")
print(f"  input_tokens:             {u2.input_tokens}")
print(f"  cache_creation_tokens:    {getattr(u2, 'cache_creation_input_tokens', 0)}")
print(f"  cache_read_tokens:        {getattr(u2, 'cache_read_input_tokens', 0)}")
print(f"  Answer: {r2.content[0].text[:100]}...")

print("""
KEY TAKEAWAYS (cert notes):
- cache_control: ephemeral  →  mark a content block for caching
- Cache TTL is 5 minutes (refreshed on each cache hit)
- Cache write costs slightly more; cache read is ~10x cheaper
- The dynamic part (user messages) goes AFTER cached blocks
""")
