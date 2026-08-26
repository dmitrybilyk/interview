# Discussion topics — _7_prompt_caching

- `cache_hit_vs_miss.py` — a cache hit is ~10% of the input token cost. What's the exact pricing ratio for Haiku vs Sonnet vs Opus?
- The cache TTL is 5 minutes. What happens to a cached prompt after 5 minutes — does it silently miss, or does the API tell you?
- `cache_invalidation.py` — what makes a cache entry invalid? Is it purely text content, or do model version and parameters matter too?
- `cache_tool_definitions.py` — caching tool definitions saves tokens on every call. At what number of tools does caching become clearly worth it?
- `cache_creation_input_tokens` vs `cache_read_input_tokens` vs `input_tokens` — what exactly does each field count?
- Can you cache the middle of a conversation, not just the system prompt? What's the constraint on where `cache_control` can go?
- If two users send the same system prompt, do they share a cache entry? Or is caching per API key?
- Prompt caching vs context window: if your cached prompt is 50k tokens and you cache it, does it still count against the 200k context window limit?
- What's the minimum block size that can be cached? Is there a minimum token threshold below which caching is ignored?
- How do you design a multi-tenant app to maximise cache hits across different users sending the same base system prompt?
