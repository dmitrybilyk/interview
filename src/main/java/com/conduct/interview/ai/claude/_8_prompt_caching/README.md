# _8 — Prompt Caching

**Cert notes section:** "2 API features reduce what you pay: Prompt caching, Token counting"  
"Cache — set up cache and save up to 90%; a single character change invalidates the cache"

## What this covers
| Concept | Detail |
|---|---|
| `cache_control: ephemeral` | Marks a content block to be cached |
| Cache write | Slightly more expensive than normal input (~20%) |
| Cache read | ~10x cheaper than normal input (~90% saving) |
| Cache TTL | 5 minutes; refreshed on every cache hit |
| Invalidation | Any byte change BEFORE the cached block kills the cache |

## Cost breakdown (Haiku, per 1M tokens)
| Type | Price |
|---|---|
| Normal input | $0.80 |
| Cache write | ~$0.96 |
| Cache read | ~$0.08 ← 10x cheaper |

## Key API
```python
# Mark a system prompt block for caching
system=[{
    "type": "text",
    "text": "... large system prompt ...",
    "cache_control": {"type": "ephemeral"}
}]

# Check cache usage in response
r.usage.cache_creation_input_tokens  # > 0 on cache miss (writing)
r.usage.cache_read_input_tokens      # > 0 on cache hit (reading)
```

## What to cache (static parts)
- System prompt (large instructions, personas, domain knowledge)
- Tool definitions (especially large schemas)
- Reference documents attached to every request

## What NOT to cache
- User messages (dynamic per request)
- Conversation history (changes every turn)

## Cache invalidation rule
```
[system_prompt CACHED][tool_list CACHED][user_message NOT CACHED]
                  ↑ change anything here = cache miss
```

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: the large cache-worthy `LARGE_SYSTEM` prompt — not runnable on its own |
| `cache_hit_vs_miss.py` | First call → `cache_creation_input_tokens` (write); second call → `cache_read_input_tokens` (hit, 10x cheaper) |
| `cache_invalidation.py` | One-character change → cache miss again |
| `cache_tool_definitions.py` | Caching tool definitions |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _8_prompt_caching && python cache_hit_vs_miss.py   # or any other file in this directory
```
