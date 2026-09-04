"""
Topic: Collect the full text after a stream ends via get_final_text()
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python _5_collect_full_text_after_stream.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Get full text after stream ends ===")
with client.messages.stream(
    model="claude-haiku-4-5-20251001",
    max_tokens=64,
    messages=[{"role": "user", "content": "Name 3 programming languages."}],
) as stream:
    # stream.text_stream is a generator — iterate it to advance the stream
    for _ in stream.text_stream:
        pass

# get_final_text() only works after the stream has been fully consumed
full = stream.get_final_text()
print("Full collected text:", full)
