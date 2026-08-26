"""
Topic: Count tokens for a simple message before sending it
Cert notes section: "2 API features reduce what you pay" + "What affects budget"
Run: ../venv/bin/python _1_count_tokens_simple.py
"""

from _common import client, estimate_cost

print("=== Count tokens — simple message ===")
model = "claude-haiku-4-5-20251001"
messages = [{"role": "user", "content": "What is the capital of France?"}]

result = client.messages.count_tokens(model=model, messages=messages)
print(f"  Input tokens: {result.input_tokens}")
print(f"  Estimated input cost: {estimate_cost(model, result.input_tokens)}")
