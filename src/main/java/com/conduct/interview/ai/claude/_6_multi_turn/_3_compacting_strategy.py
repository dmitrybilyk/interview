"""
Topic: Budget strategy — COMPACTING (summarize history into one message)
Cert notes section: Model selection and keeping multi-turn session in budget
Run: ../venv/bin/python _3_compacting_strategy.py
"""

from _common import client, count_tokens, chat, MODEL

print("=== Strategy: Compacting (summarize) ===")
long_history = []
for msg in ["I like Python.", "I work at a startup.", "I prefer dark mode."]:
    chat(long_history, msg)

print(f"  Before compacting: {count_tokens(long_history)} tokens")

# Ask Claude to summarize the conversation so far
summary_r = client.messages.create(
    model=MODEL,
    max_tokens=128,
    messages=long_history + [{
        "role": "user",
        "content": "Summarize our conversation so far in 1-2 sentences. Be concise."
    }],
)
summary = summary_r.content[0].text.strip()
print(f"  Summary: {summary}")

# Replace history with a single context message
compacted_history = [{"role": "user", "content": f"[Previous context] {summary}"},
                     {"role": "assistant", "content": "Understood. How can I help?"}]
print(f"  After compacting: {count_tokens(compacted_history)} tokens")
reply = chat(compacted_history, "What are some things you know about me?")
print(f"  Claude (from compacted context): {reply.strip()}")
