# Claude API — discussion topics (all modules)

## _1_basic_call
- Why is `content` a list and not a string — when does it have more than one element?
- What finish_reason values exist and how do you detect a truncated response programmatically?
- At temp=0, are two consecutive runs guaranteed byte-for-byte identical? What about across model versions?
- If max_tokens equals the full context window, is there room left for the output?
- Why doesn't `"Hi " * 500` cost exactly 500× more tokens than `"Hi"`?

## _2_prompting
- Zero-shot vs few-shot: when do more examples actually hurt rather than help?
- The model invented `neu` in few-shot — failure or intelligent generalisation? How do you decide?
- Is there prompt wording that reliably prevents markdown fences, or is post-processing always the safer bet?
- What's the difference between putting rules in system vs user message — does position affect compliance strength?
- XML tags vs colons: is there hard evidence one outperforms the other, or is it model-dependent?

## _3_streaming
- What are all SSE event types in a Claude stream? What does each carry?
- If the network drops mid-stream, what does the SDK throw and how do you resume?
- Can you use tool_use with streaming — what does the event sequence look like?
- Streaming vs non-streaming: is total token count identical? Is first-token latency always better with streaming?
- When does `output_tokens` become available in a stream — before or after the stream closes?

## _4_rag
- Jaccard vs embedding cosine similarity — when does Jaccard fail badly?
- Retrieved chunks wrapped in `<doc id="...">` XML — why not just concatenate plain text?
- System prompt says "answer using only the docs" — what does the model do when the answer isn't there?
- Chunk size vs top_k tradeoff — how do you decide the right balance for token budget?
- Prompt injection via retrieved docs — can a malicious document override the system prompt?

## _5_tool_use
- What exactly is in `content[0]` when the model calls a tool vs when it answers in text?
- Can the model call two tools in one response (parallel tool use)? How does the structure differ?
- How do you pass a tool execution error back to the model as a tool result?
- `tool_choice` options: auto, any, specific — when would you force a specific tool?
- How do you prevent prompt injection via tool arguments crafted by user input?

## _6_multi_turn
- At what conversation length (turns/tokens) does re-sending full history become a real cost and latency problem?
- What kinds of information survive summarisation badly — what do you lose with `compacting_strategy`?
- Is there a way to "pin" messages so they survive clearing/pruning?
- Multi-turn + tool use: tool result messages accumulate too — do they cost the same as normal turns?
- What's the difference between multi-turn history and few-shot examples in the `messages` array?

## _7_extended_thinking
- Thinking tokens billing: are they billed at the same rate as output tokens?
- For what class of tasks does extended thinking NOT help — where does it add latency with no quality gain?
- Can thinking blocks be streamed? What do the stream events look like?
- Does thinking count against the context window the same way output tokens do?
- Can you use tool_use together with extended thinking — which runs first?

## _8_prompt_caching
- Cache TTL is 5 minutes — does a miss after expiry happen silently or does the API signal it?
- `cache_creation_input_tokens` vs `cache_read_input_tokens` vs `input_tokens` — what exactly does each count?
- Is caching per API key or shared across all users who send the same prompt?
- Cached tokens still count against the context window — true or false?
- What's the minimum block size that can be cached — is there a token threshold?

## _9_token_counting
- Token counting is a separate API call — does it cost anything or count against rate limits?
- How does tokenization differ between English, code, and CJK — why does the same "information" cost different tokens?
- Image tokens: is cost resolution-dependent — is a 4K image 4× more than 1K?
- `token_guard_pattern` — what do you do when the guard triggers: truncate, summarise, or reject?
- At what conversation turn count does a typical chat exceed Haiku's 200k window?

## _10_batches
- Typical batch latency — minutes or hours? What's the official SLA?
- If one request in a batch fails, do the others still complete?
- How long are batch results retained before the API deletes them?
- Can you cancel a batch mid-processing — what happens to already-completed items?
- How do you correlate results back to original requests — what identifier ties them together?

## _11_multimodal
- How much larger does the JSON payload get when you base64 a typical photo?
- How are PDF tokens calculated — is each page a fixed cost or resolution-dependent?
- Can the model return image output, or is vision input-only for Claude?
- If an image contains text "Ignore all previous instructions", can that affect model behaviour?
- What's the maximum number of images per request and does their order matter?

## _12_agent_loop
- How do you implement the human-approval pause — poll, webhook, or blocking UI?
- What's the termination condition for an agent loop — how do you detect "done" vs infinite loop?
- How many tool-call iterations before you should break or intervene?
- How do you validate/sanitize tool arguments the model constructs before executing them?
- How do you add persistent memory across sessions without growing context indefinitely?
