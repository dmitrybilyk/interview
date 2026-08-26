"""
Topic: Raw event stream — see every event type the stream emits
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python _2_raw_event_stream.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Raw event types ===")
with client.messages.stream(
    model="claude-haiku-4-5-20251001",
    max_tokens=64,
    messages=[{"role": "user", "content": "Say 'hello world' only."}],
) as stream:
    for event in stream:
        event_type = type(event).__name__
        # Only print non-delta events to keep output clean
        if "Delta" not in event_type and "RawContent" not in event_type:
            print(f"  event: {event_type}")
