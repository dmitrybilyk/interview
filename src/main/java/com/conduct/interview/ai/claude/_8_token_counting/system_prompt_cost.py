"""
Topic: A system prompt adds tokens — measure the delta with count_tokens
Cert notes section: "2 API features reduce what you pay" + "What affects budget"
Run: ../venv/bin/python system_prompt_cost.py
"""

from _common import client

print("=== System prompt cost ===")
model = "claude-haiku-4-5-20251001"
messages = [{"role": "user", "content": "What is the capital of France?"}]
system = "You are a senior Java developer. Always answer in Java. Be concise."

result_no_sys  = client.messages.count_tokens(model=model, messages=messages)
result_with_sys = client.messages.count_tokens(model=model, system=system, messages=messages)
print(f"  Without system: {result_no_sys.input_tokens} tokens")
print(f"  With system:    {result_with_sys.input_tokens} tokens")
print(f"  System prompt cost: {result_with_sys.input_tokens - result_no_sys.input_tokens} tokens")
