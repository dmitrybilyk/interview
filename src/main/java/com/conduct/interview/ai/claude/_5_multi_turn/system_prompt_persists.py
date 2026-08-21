"""
Topic: System prompt persists across turns without growing the message history
Cert notes section: Model selection and keeping multi-turn session in budget
Run: ../venv/bin/python system_prompt_persists.py
"""

from _common import count_tokens, chat

print("=== System prompt — constant cost, not in messages list ===")
system = "The user's name is Dmytro. Always address him by name."
session = []
reply = chat(session, "Who am I?", system=system)
print(f"  Claude: {reply.strip()}")
print(f"  Messages in list: {len(session)} (system prompt not counted here)")
print(f"  Tokens: {count_tokens(session, system=system)}")
