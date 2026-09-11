# Learning Plan: from `_1_basic_call` to this agent

This project (`_20_news_agent`) is a small capstone: it's the point where
the API mechanics from the early lessons and the production concerns from
the later ones both show up in one real, deployed thing. This doc is two
parts: (1) a suggested path through the other `claude/` lessons, and
(2) exactly where in *this* codebase each of those ideas shows up, so you
can see the theory next to a working example instead of just reading about it.

## Part 1 — the other lessons, in a sensible order

They're numbered as a reasonable default order already; grouping them by
phase makes it clearer *why* that order:

### Phase A — the raw API (do these first, in order)
| # | Topic | One-line takeaway |
|---|---|---|
| `_1_basic_call` | Basic API Call | `messages.create()` → `response.content[0].text`; `stop_reason`, `usage`, `max_tokens`, temperature |
| `_2_prompting` | Prompting Techniques | Few-shot, system prompts, output constraints, XML tags — the prompt is the program |
| `_3_streaming` | Streaming | Tokens arrive as chunks; discard + retry on `stop_reason == "max_tokens"`, never keep a truncated stream |
| `_6_multi_turn` | Multi-Turn Conversation | Claude is stateless — you resend the *entire* history every call, and pay for all of it each time |
| `_9_token_counting` | Token Counting | `count_tokens()` — a free dry run to guard against oversized requests before you pay for them |

### Phase B — giving Claude more to work with
| # | Topic | One-line takeaway |
|---|---|---|
| `_4_rag` | RAG | Inject retrieved docs into the prompt; answer only from what's provided |
| `_5_tool_use` | Tool Use | Claude never executes code — it asks (`stop_reason=tool_use`), *you* run it, you send back a `tool_result` |
| `_7_extended_thinking` | Extended Thinking | Claude spends tokens on a private scratchpad before answering; thinking blocks must round-trip unchanged in tool loops, but never in normal turns |
| `_8_prompt_caching` | Prompt Caching | `cache_control: ephemeral` on static content — ~10x cheaper on a cache hit, invalidated by any earlier byte changing |
| `_10_batches` | Message Batches API | Up to 100k requests, ~50% cheaper, async — no real-time response |
| `_11_multimodal` | Multimodal | Images/PDFs are just extra content blocks, same API shape |

### Phase C — putting it in a loop
| # | Topic | One-line takeaway |
|---|---|---|
| `_12_agent_loop` | Full Agent Loop | An agent is a loop that keeps calling tools until `stop_reason == end_turn` — only worth it when decisions depend on each other |

### Phase D — no script, exam/judgment material (read, don't run)
| # | Topic | One-line takeaway |
|---|---|---|
| `_13_packaging_for_reuse` | Accelerators | Split the part that never changes from the part that does, so the next project reuses instead of rebuilds |
| `_14_contributing_back` | Picking the right channel | How to hand a packaged thing to people outside your team without it dying in a queue |
| `_15_business_to_requirements` | Business → Requirements | Turn a vague ask into two lists (functional + infra) *before* picking a platform |
| `_16_systems_lifecycle` | Systems Lifecycle | Requirements → Design → ... — the arc `_15` onward sits inside |
| `_17_deployment_and_versioning` | Where it runs, pinning versions | Two decisions to write down: which cloud, how to lock the model version |
| `_18_comparing_platforms` | Latency, Compliance, Cost | How to back up the `_17` choice with actual numbers for a review |
| `_19_trust_boundaries` | Trust Boundaries | What to check when several Claude-touching pieces are wired together |

Suggested path if you're starting cold: **A → B → C**, running every
script as you go. Then read **D** without running anything — it's
judgment calls, not API surface. Then come back here for **Part 2**.

## Part 2 — where each idea shows up in this agent

| Idea from... | Shows up here as... |
|---|---|
| `_1` basic call | `agent/providers.py`'s `_call_claude()` — the plainest possible `messages.create()` |
| `_2` output constraints | `agent/classify.py`'s prompt: *"Respond with ONLY a JSON array... No prose"* — forcing a parseable shape is the same trick as `_2`'s "reply with exactly one word" |
| `_2` policy-as-text | `agent/moods.py` — the mood rules are plain-language instructions, not code; that's deliberate (see its docstring) |
| `_6` statelessness | Every classification call is a fresh, independent `messages.create()` with no history — there's no reason to pay for multi-turn context here, since each item's verdict doesn't depend on any other call |
| `_7` extended thinking | Groq's `gpt-oss` models think before answering, same idea as `_7` — and it caused a **real bug** while building this: a big batch spent its whole `max_tokens` budget thinking and returned empty content. Fixed with `reasoning_effort: "low"` + smaller batches (`agent/providers.py`, `agent/config.py`'s `CLASSIFY_BATCH_SIZE`) |
| `_9` token counting | Not used directly, but `agent/providers.py` logs `prompt_tokens`/`completion_tokens` from every response for the same reason `_9` exists: visibility into what you're paying for. **Exercise:** add a `count_tokens()` pre-check before a big cold-start batch |
| `_12` agent loop vs. single call | `agent/classify.py`'s docstring makes the comparison explicit: one decision per item, no dependency between items → one batched call, not a loop |
| `_13` packaging for reuse | The whole `agent/` package *is* this lesson applied: `providers.py`'s `call_llm()` is exactly "split the part that never changes (call an LLM, get text back) from the part that does (which provider, which prompt)" |
| `_16`/`_17` lifecycle & deployment | `deploy.sh` + this doc's own history: requirements ("subscribe via Telegram") → design (webhook + timer vs. polling) → deploy (systemd + nginx path proxy, because the platform's own constraints — closed ports — ruled out the naive approach) |
| `_18` comparing platforms | The IP:port vs. nginx-proxy decision in `README.md`'s Deploy section is a mini version of `_18`: the "obvious" approach (open a port) was ruled out by an actual constraint (cloud security list), not preference |
| `_19` trust boundaries | **Worth studying, not yet fixed here:** `app.py`'s `/telegram-webhook` route accepts POST requests from anyone, not just Telegram — it never verifies the request actually came from Telegram's servers. `_19` is exactly about auditing this kind of boundary. **Exercise:** add Telegram's `secret_token` header check (see `setWebhook`'s `secret_token` param) and reject requests without it |

## A few concrete exercises, if you want to go further

1. **Add a third mood.** Edit only `agent/moods.py`. Confirm with
   `cli.py your-mood --verbose` that nothing else needed to change —
   that's the payoff of the `agent/` split.
2. **Try `_9`'s token counting here.** Before a cold-start batch (empty
   `classification_cache.json`), call `count_tokens()` and log the
   estimate vs. what actually got used.
3. **Try `_8`'s prompt caching.** The mood rules text in the prompt is
   identical across every call for a given mood — a real candidate for
   `cache_control: ephemeral` if you switch back to Claude and the batch
   sizes grow.
4. **Close the `_19` gap above** — add webhook signature verification.
5. **Try `_5` tool use.** Give the agent a tool like
   "fetch_full_article(url)" and let it decide, per item, whether the
   RSS description is enough or it needs the full page before judging —
   now you have a real multi-step decision, which is when `_12`'s loop
   pattern (not this project's single call) becomes the right shape.
