"""
Topic: Budget strategy — PRUNING (drop old messages when context grows)
Cert notes section: Model selection and keeping multi-turn session in budget
Run: ../venv/bin/python pruning_strategy.py
"""

from _common import count_tokens, chat

print("=== Strategy: Pruning (keep last N turns) ===")
MAX_MESSAGES = 4   # keep last 2 exchanges (4 messages)

full_history = []
for user_msg in ["Fact 1: sky is blue.", "Fact 2: grass is green.", "What was Fact 1?"]:
    chat(full_history, user_msg)

print(f"  Full history: {len(full_history)} messages, {count_tokens(full_history)} tokens")

# Prune: keep only the last MAX_MESSAGES messages
pruned = full_history[-MAX_MESSAGES:]
print(f"  After pruning: {len(pruned)} messages, {count_tokens(pruned)} tokens")
# Note: memory of early turns is lost after pruning
