"""
Topic: Token counting — estimate cost before sending, context budget management
Cert notes section: "2 API features reduce what you pay" + "What affects budget"
Run: ../venv/bin/python script.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Approximate pricing (per 1M tokens, Aug 2025)
PRICES = {
    "claude-haiku-4-5-20251001": {"in": 0.80,  "out": 4.00},
    "claude-sonnet-4-6":         {"in": 3.00,  "out": 15.00},
    "claude-opus-4-7":           {"in": 15.00, "out": 75.00},
}

def estimate_cost(model: str, input_tokens: int, output_tokens: int = 0) -> str:
    p = PRICES[model]
    cost = (input_tokens / 1_000_000 * p["in"]) + (output_tokens / 1_000_000 * p["out"])
    return f"${cost:.6f}"

# ── 1. Count tokens for a simple message ──────────────────────────────────
print("=== 1. Count tokens — simple message ===")
model = "claude-haiku-4-5-20251001"
messages = [{"role": "user", "content": "What is the capital of France?"}]

result = client.messages.count_tokens(model=model, messages=messages)
print(f"  Input tokens: {result.input_tokens}")
print(f"  Estimated input cost: {estimate_cost(model, result.input_tokens)}")

# ── 2. System prompt adds tokens ──────────────────────────────────────────
print("\n=== 2. System prompt cost ===")
system = "You are a senior Java developer. Always answer in Java. Be concise."
result_no_sys  = client.messages.count_tokens(model=model, messages=messages)
result_with_sys = client.messages.count_tokens(model=model, system=system, messages=messages)
print(f"  Without system: {result_no_sys.input_tokens} tokens")
print(f"  With system:    {result_with_sys.input_tokens} tokens")
print(f"  System prompt cost: {result_with_sys.input_tokens - result_no_sys.input_tokens} tokens")

# ── 3. Tools cost tokens ──────────────────────────────────────────────────
print("\n=== 3. Tools add tokens too ===")
tools = [
    {
        "name": "search_web",
        "description": "Search the web for up-to-date information on any topic.",
        "input_schema": {
            "type": "object",
            "properties": {"query": {"type": "string", "description": "Search query"}},
            "required": ["query"]
        }
    },
    {
        "name": "run_sql",
        "description": "Execute a read-only SQL query against the database.",
        "input_schema": {
            "type": "object",
            "properties": {"sql": {"type": "string"}},
            "required": ["sql"]
        }
    }
]
result_with_tools = client.messages.count_tokens(model=model, tools=tools, messages=messages)
print(f"  Without tools: {result_no_sys.input_tokens} tokens")
print(f"  With 2 tools:  {result_with_tools.input_tokens} tokens")
print(f"  Tools overhead: {result_with_tools.input_tokens - result_no_sys.input_tokens} tokens")

# ── 4. Conversation history grows → tokens grow ───────────────────────────
print("\n=== 4. Token growth over conversation turns ===")
history = []
topics = [
    "Tell me about Python.",
    "What about Java?",
    "Compare them in one sentence.",
    "What's the best for web APIs?",
]
for topic in topics:
    history.append({"role": "user", "content": topic})
    r = client.messages.count_tokens(model=model, messages=history)
    print(f"  After {len(history)} messages: {r.input_tokens} tokens  (~{estimate_cost(model, r.input_tokens)})")
    history.append({"role": "assistant", "content": "(placeholder response)"})

# ── 5. Compare cost across models ─────────────────────────────────────────
print("\n=== 5. Same request — cost across models ===")
big_messages = [{"role": "user", "content": "Explain microservices in detail. " * 50}]
for mdl in PRICES:
    r = client.messages.count_tokens(model=mdl, messages=big_messages)
    cost = estimate_cost(mdl, r.input_tokens, output_tokens=500)
    print(f"  {mdl:<40} {r.input_tokens:>6} tokens  cost≈{cost}")

# ── 6. Use count_tokens as a guard before sending ─────────────────────────
print("\n=== 6. Guard pattern — abort if too expensive ===")
MAX_INPUT_TOKENS = 5000
check = client.messages.count_tokens(model=model, messages=big_messages)
if check.input_tokens > MAX_INPUT_TOKENS:
    print(f"  BLOCKED: {check.input_tokens} tokens exceeds limit of {MAX_INPUT_TOKENS}. Truncate or summarize first.")
else:
    print(f"  OK: {check.input_tokens} tokens — sending request.")

print("""
KEY TAKEAWAYS (cert notes — "What affects budget"):
1. Model selection — Haiku is 19x cheaper input than Opus
2. Prompt size — system prompt + tools + history all count
3. Number of tool calls — each round trip adds tokens
4. Streaming vs batch — same token cost, batch has latency trade-off
Use count_tokens() BEFORE the real call to guard expensive requests.
""")
