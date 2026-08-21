"""
Topic: Normal multi-turn conversation — watch token count grow
Cert notes section: Model selection and keeping multi-turn session in budget
Run: ../venv/bin/python multi_turn_basics.py
"""

from _common import count_tokens, chat

print("=== Multi-turn: token growth ===")
history = []
turns = [
    "My name is Dmytro. Remember that.",
    "What programming languages do you know?",
    "What's my name?",   # tests memory across turns
]
for user_msg in turns:
    tokens_before = count_tokens(history)
    reply = chat(history, user_msg)
    tokens_after = count_tokens(history)
    print(f"  User: {user_msg}")
    print(f"  Claude: {reply.strip()}")
    print(f"  Tokens in context: {tokens_before} → {tokens_after}\n")
