"""
Topic: Multi-turn conversation, context growth, 4 budget strategies
Cert notes section: Model selection and keeping multi-turn session in budget
  Strategies: Pruning, Compacting, Clearing, Sub-agent handoffs
Run: ../venv/bin/python script.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])
MODEL = "claude-haiku-4-5-20251001"

def count_tokens(messages: list, system: str = "") -> int:
    r = client.messages.count_tokens(model=MODEL, system=system, messages=messages)
    return r.input_tokens

def chat(messages: list, user_input: str, system: str = "") -> str:
    messages.append({"role": "user", "content": user_input})
    r = client.messages.create(
        model=MODEL, max_tokens=256, system=system, messages=messages
    )
    reply = r.content[0].text
    messages.append({"role": "assistant", "content": reply})
    return reply

# ── 1. Normal multi-turn — watch token count grow ─────────────────────────
print("=== 1. Multi-turn: token growth ===")
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

# ── 2. Strategy: PRUNING — drop old messages when context grows ────────────
print("=== 2. Strategy: Pruning (keep last N turns) ===")
MAX_MESSAGES = 4   # keep last 2 exchanges (4 messages)

full_history = []
for user_msg in ["Fact 1: sky is blue.", "Fact 2: grass is green.", "What was Fact 1?"]:
    chat(full_history, user_msg)

print(f"  Full history: {len(full_history)} messages, {count_tokens(full_history)} tokens")

# Prune: keep only the last MAX_MESSAGES messages
pruned = full_history[-MAX_MESSAGES:]
print(f"  After pruning: {len(pruned)} messages, {count_tokens(pruned)} tokens")
# Note: memory of early turns is lost after pruning

# ── 3. Strategy: COMPACTING — summarize history into one message ───────────
print("\n=== 3. Strategy: Compacting (summarize) ===")
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

# ── 4. Strategy: CLEARING — fresh session ─────────────────────────────────
print("\n=== 4. Strategy: Clearing (fresh session) ===")
old_session = [
    {"role": "user",      "content": "I am Dmytro."},
    {"role": "assistant", "content": "Hello Dmytro!"},
]
new_session = []   # completely fresh — old context is gone
reply = chat(new_session, "What's my name?")
print(f"  After clearing — Claude: {reply.strip()}")
print(f"  (Claude doesn't know name — fresh session has no history)")

# ── 5. system prompt persists without growing the history ─────────────────
print("\n=== 5. System prompt — constant cost, not in messages list ===")
system = "The user's name is Dmytro. Always address him by name."
session = []
reply = chat(session, "Who am I?", system=system)
print(f"  Claude: {reply.strip()}")
print(f"  Messages in list: {len(session)} (system prompt not counted here)")
print(f"  Tokens: {count_tokens(session, system=system)}")
