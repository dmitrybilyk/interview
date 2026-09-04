"""
Topic: Budget strategy — CLEARING (start a completely fresh session)
Cert notes section: Model selection and keeping multi-turn session in budget
Run: ../venv/bin/python _4_clearing_strategy.py
"""

from _common import chat

print("=== Strategy: Clearing (fresh session) ===")
new_session = []   # completely fresh — old context is gone
reply = chat(new_session, "What's my name?")
print(f"  After clearing — Claude: {reply.strip()}")
print(f"  (Claude doesn't know name — fresh session has no history)")
