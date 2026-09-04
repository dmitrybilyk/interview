"""
Topic: Streaming with a system prompt
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python _3_streaming_with_system_prompt.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Stream with system prompt ===")
with client.messages.stream(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    system="You are a haiku poet. Every response must be a haiku. and also every row prepend with OMG",
    messages=[{"role": "user", "content": "Write about coffee."}],
) as stream:
    for text in stream.text_stream:
        print(text, end="", flush=True)
print()
