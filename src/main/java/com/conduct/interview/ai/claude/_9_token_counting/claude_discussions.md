# Discussion topics — _8_token_counting

- `count_tokens_simple.py` — token counting is a separate API call. Does it cost anything? Does it count against rate limits?
- `system_prompt_cost.py` — the system prompt is re-sent every turn. If it's 10k tokens and you have 50 turns, that's 500k tokens just for the system prompt. What's the mitigation?
- `token_growth_over_turns.py` — at what conversation length (turns) should you start worrying about the window for Haiku (200k)? For Sonnet (200k)?
- `token_guard_pattern.py` — what's the pattern: count first, then decide whether to send. How do you handle the race condition where the count is stale by the time you send?
- `tools_cost.py` — tool definitions cost tokens even if the model never calls them. Is there a way to send tools only when relevant?
- `cost_across_models.py` — Haiku is cheapest but least capable. What's the practical decision framework for choosing a model per task type?
- How does tokenization differ between English, code, and CJK text? Why does the same "amount" of information cost different tokens?
- Images cost tokens too — how is an image's token cost calculated? Is it resolution-dependent?
- Can you use token counting to estimate cost before a batch job without actually running it?
- `token_guard_pattern.py` — if you guard on token count, what do you do when the guard triggers — truncate, summarize, or reject the request?
