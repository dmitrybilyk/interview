"""
Shared client and helpers for _5_multi_turn.
Not runnable on its own — imported by the strategy scripts in this directory.
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
