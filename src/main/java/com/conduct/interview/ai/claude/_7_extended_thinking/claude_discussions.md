# Discussion topics — _6_extended_thinking

- `thinking_enabled.py` — thinking tokens appear in the response but are not billed the same way as output tokens. What are the exact billing rules?
- `thinking_disabled_baseline.py` — for what class of tasks does thinking NOT help or even hurt (e.g., adds latency with no quality gain)?
- `thinking_logic_puzzle.py` — thinking is visible in the response as a `thinking` content block. Can you read it and use it as context in the next turn?
- What's the minimum `budget_tokens` for thinking? What happens if you set it too low for a complex problem?
- Extended thinking vs chain-of-thought prompting ("think step by step") — when is the built-in thinking better than prompting for CoT?
- Can thinking be streamed? What do the stream events look like for a thinking block?
- Does thinking increase the effective context window usage? If thinking produces 5k tokens, does that count against the 200k limit?
- Can you use tool_use together with extended thinking? Which runs first?
- `thinking` blocks in the response — are they always in English, or do they follow the user's language?
- When would you show the thinking to the end user vs hide it? What are the UX patterns for surfacing reasoning?
