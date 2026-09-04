"""
Topic: Prompt caching — a single character change invalidates the cache
Cert notes section: "A single character change invalidates the cache"
Run: ../venv/bin/python _2_cache_invalidation.py
"""

import os
from anthropic import Anthropic
from _common import LARGE_SYSTEM

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

def call(label, system_text, question="Hi"):
    r = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=64,
        system=[{"type": "text", "text": system_text, "cache_control": {"type": "ephemeral"}}],
        messages=[{"role": "user", "content": question}],
    )
    u = r.usage
    print(f"{label}")
    print(f"  cache_creation_tokens: {getattr(u, 'cache_creation_input_tokens', 0)}")
    print(f"  cache_read_tokens:     {getattr(u, 'cache_read_input_tokens', 0)}")

# 1. Write cache
call("=== Call 1 — original prompt → cache WRITE ===", LARGE_SYSTEM)

# 2. Hit cache (same prompt)
call("=== Call 2 — same prompt → cache HIT ===", LARGE_SYSTEM)

# 3. One character change → cache miss, new cache written
call("=== Call 3 — one '.' appended → cache MISS (re-created) ===", LARGE_SYSTEM + ".")
