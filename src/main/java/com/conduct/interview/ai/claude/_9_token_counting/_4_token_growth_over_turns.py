"""
Topic: Conversation history grows → token count grows every turn
Cert notes section: "2 API features reduce what you pay" + "What affects budget"
Run: ../venv/bin/python _4_token_growth_over_turns.py
"""

from _common import client, estimate_cost

print("=== Token growth over conversation turns ===")
model = "claude-haiku-4-5-20251001"
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
