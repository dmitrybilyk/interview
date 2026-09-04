# _8 — Prompt Caching

Mark static content with `cache_control: ephemeral`. Second call reads from cache: ~10x cheaper.  
Any byte change BEFORE the cached block → cache miss, full price.  
Cache TTL: 5 min, refreshed on every hit.

```python
system=[{
    "type": "text",
    "text": "... large static prompt ...",
    "cache_control": {"type": "ephemeral"}
}]

# Check result:
r.usage.cache_creation_input_tokens  # > 0 → cache was written (slightly more expensive)
r.usage.cache_read_input_tokens      # > 0 → cache hit (~90% cheaper)
```

**Minimum cacheable block:** 1024 tokens for Sonnet/Opus (Haiku 4.5 does not support caching). Below the threshold `cache_control` is silently ignored — both cache counters stay 0.

**Cache this:** system prompt, tool definitions, large reference docs (static per request).  
**Don't cache:** user messages, conversation history (dynamic, change every turn).

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: `LARGE_SYSTEM` prompt — not runnable |
| `cache_hit_vs_miss.py` | First call = write; second call = hit (10x cheaper) |
| `cache_invalidation.py` | One-character change → cache miss |
| `cache_tool_definitions.py` | Caching tool definitions |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _8_prompt_caching && ../venv/bin/python cache_hit_vs_miss.py
```
