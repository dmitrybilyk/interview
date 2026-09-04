# Discussion topics — _7_extended_thinking

Each question below now has a concise answer. See `README.md` for the `budget_tokens`
vs `adaptive`/`effort` API-drift note, and `_4_thinking_multi_step_tool_use.py` for a
runnable demo of the tool_use interaction.

**Q: `thinking_enabled.py` — thinking tokens appear in the response but are not billed
the same way as output tokens. What are the exact billing rules?**
They ARE billed as normal output tokens — there's no separate, cheaper thinking rate.
`response.usage.output_tokens_details.thinking_tokens` breaks out how many of the
total output tokens were spent thinking, purely for visibility.

**Q: `thinking_disabled_baseline.py` — for what class of tasks does thinking NOT help
or even hurt?**
Simple lookups, short factual answers, formatting/rewriting tasks, single-fact Q&A —
anything with no real reasoning chain. Thinking adds latency and token cost with no
quality gain there, and on `claude-opus-5` disabling it entirely (rather than just
lowering `effort`) can backfire — see the README's Common Pitfalls note on the two
failure modes of `thinking: {"type": "disabled"}`.

**Q: `thinking_logic_puzzle.py` — thinking is visible as a `thinking` content block.
Can you read it and use it as context in the next turn?**
Depends which "next turn":
- *Same, still-open tool-use loop:* you MUST send it back unchanged as part of
  `response.content` — the API verifies a signature tying the thinking to the
  tool call it produced. See `_4_thinking_multi_step_tool_use.py`.
- *A genuinely new, separate question after the turn is fully finished:* no, you
  don't need to resend that old thinking, and manually copy-pasting its text into
  a new prompt as "context" is not a supported/needed pattern — the model doesn't
  rely on it once the turn is done.

**Q: What's the minimum `budget_tokens`? What happens if you set it too low for a
complex problem?**
Minimum is 1024, and it must be less than `max_tokens`. Too low for the problem at
hand means Claude's reasoning gets cut short before it reaches a solid conclusion —
you get a worse or inconsistent final answer, not an error. (This whole problem goes
away with `thinking: {"type": "adaptive"}` on current models — see README — since
there's no fixed ceiling for Claude to run out of.)

**Q: Extended thinking vs. chain-of-thought prompting ("think step by step") — when
is the built-in thinking better?**
Built-in thinking is a separate scratchpad the model was specifically trained to use
for reasoning before answering — it isn't part of the visible final text, so it can't
leak into or get cut off by a `stop_sequence`, and Claude uses it more reliably than
a prompted "think step by step," which just asks the model to reason inline in its
visible answer. Prompted CoT still has a place on models where extended thinking
isn't available at all, or for very lightweight nudges that don't warrant the extra
cost/latency of full thinking.

**Q: Can thinking be streamed? What do the stream events look like?**
Yes. A thinking block streams as a `content_block_start` (`type: "thinking"`),
followed by `content_block_delta` events of type `thinking_delta` carrying incremental
text, then a `signature_delta` event carrying the verification signature, then
`content_block_stop` — mirroring how `text` blocks stream, just with an extra
signature step at the end.

**Q: Does thinking increase effective context window usage? If thinking produces 5k
tokens, does that count against the limit?**
Yes — thinking tokens are part of that response's output and count toward context
like any other generated tokens. What you resend on the *next* request is what
matters going forward: while a tool-use loop is open you must resend the in-progress
turn's thinking (see above), so it keeps counting; once a turn is closed you're free
to not resend it, so it stops contributing to future requests' input size.

**Q: Can you use `tool_use` together with extended thinking? Which runs first?**
Yes. Thinking runs before Claude decides to call a tool, and — with interleaved
thinking (auto-enabled by `thinking: {"type": "adaptive"}` on Opus 4.6/Sonnet
4.6+/current models) — can run again after a tool result comes back, before the next
tool call or the final answer. Full runnable example: `_4_thinking_multi_step_tool_use.py`.

**Q: Are `thinking` blocks always in English, or do they follow the user's language?**
Not guaranteed to be English — thinking tends to follow the conversation's language,
but it's internal reasoning the model wasn't strictly trained to keep in any one
language, so don't build logic that parses or depends on its language or exact
wording.

**Q: When would you show thinking to the end user vs. hide it? UX patterns?**
Show it (`thinking: {..., "display": "summarized"}`) when transparency/trust matters —
coding agents, research tools, anything where "show your work" helps the user verify
or debug the answer. Hide it (`"display": "omitted"`, the default on current models)
for consumer-facing chat, where raw or even summarized reasoning is usually just
noise and adds a perceived delay before the "real" answer shows up.
