# Discussion topics — _5_multi_turn

- `multi_turn_basics.py` — the API is stateless, you re-send everything each call. At what conversation length does this become a real problem (latency, cost)?
- `system_prompt_persists.py` — is there a way to change the system prompt mid-conversation? What's the safest pattern if requirements change?
- `clearing_strategy.py` — when you clear history, the model loses context. What's the user experience impact? When is clearing the right call vs summarising?
- `compacting_strategy.py` — summarising old turns compresses context but can lose details. What kinds of information survive summarisation badly?
- `pruning_strategy.py` — removing old messages vs summarising: what are the tradeoffs for a customer support bot vs a coding assistant?
- How do you implement "forget the last message" — does deleting the last user+assistant pair from history work cleanly?
- Multi-turn + tool use: the tool result messages also accumulate in history. Do they cost the same as normal turns?
- `token_growth_over_turns.py` (in _8) — at what turn count does a typical conversation exceed Haiku's 200k window?
- What's the difference between a multi-turn conversation and few-shot examples? They look the same in the `messages` array — what distinguishes them semantically?
- Is there a way to "pin" certain messages so they survive clearing/pruning — e.g., always keep the first user explanation of the task?
