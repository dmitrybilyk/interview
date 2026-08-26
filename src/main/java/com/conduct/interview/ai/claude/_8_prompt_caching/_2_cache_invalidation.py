"""
Topic: Prompt caching — a single character change invalidates the cache
Cert notes section: "A single character change invalidates the cache"
Run: ../venv/bin/python _2_cache_invalidation.py
"""

import os
from anthropic import Anthropic
from _common import LARGE_SYSTEM

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Cache invalidation (one character changed) ===")
MODIFIED_SYSTEM = LARGE_SYSTEM + "."   # one character appended

r3 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=64,
    system=[{
        "type": "text",
        "text": MODIFIED_SYSTEM,   # different → cache miss again
        "cache_control": {"type": "ephemeral"}
    }],
    messages=[{"role": "user", "content": "Hi"}],
)
u3 = r3.usage
print(f"  cache_creation_tokens: {getattr(u3, 'cache_creation_input_tokens', 0)}  ← re-created")
print(f"  cache_read_tokens:     {getattr(u3, 'cache_read_input_tokens', 0)}")
