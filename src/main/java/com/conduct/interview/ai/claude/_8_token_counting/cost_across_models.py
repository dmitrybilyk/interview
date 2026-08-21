"""
Topic: Same request — compare estimated cost across models
Cert notes section: "2 API features reduce what you pay" + "What affects budget"
Run: ../venv/bin/python cost_across_models.py
"""

from _common import client, PRICES, estimate_cost

print("=== Same request — cost across models ===")
big_messages = [{"role": "user", "content": "Explain microservices in detail. " * 50}]
for mdl in PRICES:
    r = client.messages.count_tokens(model=mdl, messages=big_messages)
    cost = estimate_cost(mdl, r.input_tokens, output_tokens=500)
    print(f"  {mdl:<40} {r.input_tokens:>6} tokens  cost≈{cost}")
