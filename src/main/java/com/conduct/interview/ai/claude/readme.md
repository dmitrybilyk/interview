# Claude API — hands-on practice

Personal lab for the Claude Certified Developer prep. One folder per concept, in
the order I actually learned them. `claude-certification-notes.md` is the real
study doc — these folders are where I ran the ideas to see if they actually
work the way the notes say.

## Setup

    source setup.sh

Creates `venv/`, installs `anthropic`, loads the key from `key.txt` (one line,
not committed). Run it once per machine and again in every new shell.

Check the environment is actually working before you sit down to a lesson:

    python3 check_setup.py          # free — python version, package, key present
    python3 check_setup.py --live   # + one real API call, costs a few tokens

## Lessons

| Folder | Topic |
|---|---|
| `_1_basic_call` | Minimal `messages.create()` → reading the response |
| `_2_prompting` | Zero/few/multi-shot, system prompts, forcing structured output |
| `_3_streaming` | Same call, tokens arrive as chunks |
| `_4_rag` | Feeding Claude your own data instead of relying on training knowledge |
| `_5_tool_use` | Claude asks you to run code, you run it, you send the result back |
| `_6_multi_turn` | Claude is stateless — you resend history — pruning/compacting to control cost |
| `_7_extended_thinking` | Private reasoning scratchpad before the visible answer |
| `_8_prompt_caching` | `cache_control: ephemeral` — ~10x cheaper repeat calls |
| `_9_token_counting` | `count_tokens()` — dry-run cost estimate, no request sent |
| `_10_batches` | Async batch API, up to 100k requests, ~50% cheaper |
| `_11_multimodal` | Images/PDFs as extra content blocks |
| `_12_agent_loop` | The actual agent loop: tools → execute → send result → repeat until done, with a human-approval gate before destructive actions |
| `_13_packaging_for_reuse` | Turning the _12 agent loop into something reusable: settings moved into a config file, a tool list turned into a document a new person can read, and a test set that checks it still works before you'd switch models |
| `_14_contributing_back` | No script — decision-making notes on where to submit shared code (your own tool's repo / the Cookbook / an existing example's repo) and the 4 things a maintainer needs to trust it: one focused thing, a working example, a test, and stated assumptions — plus the rights check that comes before any of that |
| `_15_business_to_requirements` | No script — turning a vague business ask into a checkable functional list and an infrastructure list (latency, scale, residency, identity), written down before picking a deployment platform. Includes a blank template to reuse |
| `_16_systems_lifecycle` | No script — the 7-phase arc (requirements → design → build → test → deploy → operate → iterate) the rest of the module's topics sit inside, and why you don't skip a gate between phases |
| `_17_deployment_and_versioning` | No script — which platform to run on (usually decided by the customer's existing cloud), and pinning a model version instead of a moving alias so an upstream change can't silently hit production |
| `_18_comparing_platforms` | No script — backing up the `_17` platform choice with real latency/compliance/cost numbers so it survives a procurement review, not just "it's what I picked" |
| `_19_trust_boundaries` | No script — when you wire several Claude pieces together (API → Claude Code task → MCP server), treat every connection point as a place identity/secrets/untrusted input can leak through, and scope each piece to least privilege. Also has `takeaways.md` — the fast-recall summary + glossary for the whole `_13`–`_19` arc |

Every lesson folder is self-contained: the script(s) to run, a `README.md`
(what it demonstrates + the exact run command), and `claude_discussions.md`
(the questions I didn't have a solid answer to, or bugs I actually hit).

## Other files here

- `claude-certification-notes.md` — the study notes, organized by cert topic
- `claude_discussions.md` — open questions not tied to one specific lesson
- `test_tools.py` — standalone tool-use demo, written before the lessons got split into folders
- `check_setup.py` — environment sanity check, see Setup above

## Worth remembering

`_12_agent_loop`: `stop_reason == "end_turn"` with no tool call is **not**
proof the task is done. Claude can ask "confirm you want me to order this?"
as plain text, hit `end_turn`, and the loop treats that as finished — the
human-approval gate never fires because it only guards real tool calls, not
text questions. Fix: tell it explicitly in the system prompt that approval
is already handled outside the model, so it should just call the tool.
Full writeup in `_12_agent_loop/claude_discussions.md`.
