"""
Topic: Guard pattern — abort a request if it's too expensive before sending it
Cert notes section: "2 API features reduce what you pay" + "What affects budget"
Run: ../venv/bin/python token_guard_pattern.py
"""

from _common import client

print("=== Guard pattern — abort if too expensive ===")
model = "claude-haiku-4-5-20251001"
big_messages = [{"role": "user", "content": "Explain microservices in detail. " * 50}]

MAX_INPUT_TOKENS = 5000
check = client.messages.count_tokens(model=model, messages=big_messages)
if check.input_tokens > MAX_INPUT_TOKENS:
    print(f"  BLOCKED: {check.input_tokens} tokens exceeds limit of {MAX_INPUT_TOKENS}. Truncate or summarize first.")
else:
    print(f"  OK: {check.input_tokens} tokens — sending request.")

print("""
KEY TAKEAWAYS (cert notes — "What affects budget"):
1. Model selection — Haiku is far cheaper input than Opus
2. Prompt size — system prompt + tools + history all count
3. Number of tool calls — each round trip adds tokens
4. Streaming vs batch — same token cost, batch has latency trade-off
Use count_tokens() BEFORE the real call to guard expensive requests.
""")
