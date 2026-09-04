"""
Topic: Extended thinking across MULTIPLE tool-use steps ("interleaved thinking")
Cert notes section: Extended thinking — "Effort — how deep to think"
Run: ../venv/bin/python _4_thinking_multi_step_tool_use.py

Answers the open questions from claude_discussions.md:
  "Can you use tool_use together with extended thinking? Which runs first?"
  "Can you read the thinking block and use it as context in the next turn?"

Rule: while a tool-use loop is still in progress, any `thinking` block Claude
emits alongside a `tool_use` block MUST be sent back to the API UNCHANGED, as
part of `response.content`, in the very next request. The API verifies a
signature on the thinking block to confirm the reasoning really led to that
tool call — stripping or rewriting it silently breaks that chain. Once a turn
is fully finished (no more pending tool calls), you do not need to resend that
turn's old thinking blocks for a brand-new question.

Note: this uses `thinking={"type": "adaptive"}` (current API) instead of the
`budget_tokens` you'll see in _2_/_3_ in this folder. `budget_tokens` was a
hard token ceiling YOU picked by hand. Adaptive thinking lets Claude decide
how much to think on its own; you steer overall depth with output_config's
`effort` level (low/medium/high/xhigh/max) instead of a raw token number.
`budget_tokens` is rejected outright on current models (claude-opus-5,
claude-sonnet-5, claude-fable-5, claude-opus-4-7/4-8) — it still works on
claude-sonnet-4-6 (used here) only as a deprecated transitional escape hatch.
"""

import os
import json
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

TOOLS = [
    {
        "name": "get_capital",
        "description": "Returns the capital city of a country.",
        "input_schema": {
            "type": "object",
            "properties": {"country": {"type": "string", "description": "Country name"}},
            "required": ["country"],
        },
    },
    {
        "name": "get_population",
        "description": "Returns the population of a city.",
        "input_schema": {
            "type": "object",
            "properties": {"city": {"type": "string", "description": "City name"}},
            "required": ["city"],
        },
    },
]


def get_capital(country: str) -> dict:
    fake_db = {"france": "Paris", "japan": "Tokyo", "germany": "Berlin"}
    return {"country": country, "capital": fake_db.get(country.lower(), "unknown")}


def get_population(city: str) -> dict:
    fake_db = {"paris": 2_161_000, "tokyo": 13_960_000, "berlin": 3_645_000}
    return {"city": city, "population": fake_db.get(city.lower(), "unknown")}


def execute_tool(name: str, inputs: dict) -> str:
    if name == "get_capital":
        return json.dumps(get_capital(**inputs))
    if name == "get_population":
        return json.dumps(get_population(**inputs))
    return json.dumps({"error": f"Unknown tool: {name}"})


# Deliberately needs TWO sequential tool calls — Claude must reason about the
# result of the first call before it knows what to ask for in the second.
QUESTION = (
    "What is the population of the capital of France? "
    "Then tell me what that number would be if it doubled."
)

messages = [{"role": "user", "content": QUESTION}]
step = 0

while True:
    step += 1
    print(f"\n=== Step {step} — sending {len(messages)} message(s) in history ===")

    response = client.messages.create(
        model="claude-sonnet-4-6",
        max_tokens=8000,
        thinking={"type": "adaptive"},  # auto-enables interleaved thinking on this model
        tools=TOOLS,
        messages=messages,
    )

    print(f"stop_reason: {response.stop_reason}")

    for block in response.content:
        if block.type == "thinking":
            preview = block.thinking[:200] + ("..." if len(block.thinking) > 200 else "")
            print(f"  [thinking, {len(block.thinking)} chars] {preview}")
        elif block.type == "tool_use":
            print(f"  [tool_use] {block.name}({block.input})")
        elif block.type == "text":
            print(f"  [text] {block.text}")

    tool_use_blocks = [b for b in response.content if b.type == "tool_use"]
    if not tool_use_blocks:
        break  # Claude is done — final answer was in the text block(s) above

    # CRITICAL: append response.content UNCHANGED. This is what preserves the
    # thinking block(s) that justified this tool call, interleaved with the
    # tool_use block(s), so the API can verify the reasoning->action chain on
    # the next request.
    messages.append({"role": "assistant", "content": response.content})

    tool_results = []
    for block in tool_use_blocks:
        result = execute_tool(block.name, block.input)
        print(f"  [tool_result] {result}")
        tool_results.append({
            "type": "tool_result",
            "tool_use_id": block.id,  # MUST match block.id exactly
            "content": result,
        })

    # All tool_results for this turn go back in a single user message.
    messages.append({"role": "user", "content": tool_results})

print("""
KEY TAKEAWAYS (cert notes):
- Extended thinking + tool_use CAN be combined. Thinking runs before Claude
  decides to call a tool, and can run again after each tool_result comes
  back, before the next tool call or the final answer ("interleaved thinking").
- While the tool-use loop is still in progress, you MUST echo response.content
  back unmodified as the assistant turn — including any thinking blocks. The
  API checks a signature on each thinking block to confirm the reasoning
  really led to that tool call; dropping or rewriting it breaks the chain.
- Once a turn is fully finished (no more pending tool calls), a brand-new,
  separate question does NOT need that old turn's thinking blocks resent.
- Thinking tokens are billed as normal output tokens — see usage below.
""")
print(response.usage)
