"""
Topic: Streaming — text deltas, tool_use chunks, handling incomplete stream
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python script.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# ── 1. Basic text streaming — tokens arrive as they are generated ──────────
print("=== 1. Text streaming ===")
print("(each chunk printed as it arrives)\n")

with client.messages.stream(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    messages=[{"role": "user", "content": "Count slowly from 1 to 5, one number per line."}],
) as stream:
    for text in stream.text_stream:
        print(text, end="", flush=True)

print("\n\n--- stream ended ---")
final = stream.get_final_message()
print(f"Stop reason: {final.stop_reason}")
print(f"Total tokens: in={final.usage.input_tokens} out={final.usage.output_tokens}")

# ── 2. Raw event stream — see every event type ────────────────────────────
print("\n=== 2. Raw event types ===")
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

# ── 3. Streaming with system prompt ───────────────────────────────────────
print("\n=== 3. Stream with system prompt ===")
with client.messages.stream(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    system="You are a haiku poet. Every response must be a haiku.",
    messages=[{"role": "user", "content": "Write about coffee."}],
) as stream:
    for text in stream.text_stream:
        print(text, end="", flush=True)
print()

# ── 4. Incomplete stream simulation ───────────────────────────────────────
# Cert note: "If looping is done but message stop is not received — discard the whole message"
print("\n=== 4. Incomplete stream — stop_reason check ===")
try:
    with client.messages.stream(
        model="claude-haiku-4-5-20251001",
        max_tokens=8,    # tiny limit to force max_tokens stop
        messages=[{"role": "user", "content": "Write a very long essay about the universe."}],
    ) as stream:
        collected = ""
        for text in stream.text_stream:
            collected += text

    final = stream.get_final_message()
    if final.stop_reason == "max_tokens":
        print("Stream cut short (max_tokens). Discarding incomplete message.")
        print(f"Incomplete text: '{collected}'")
        print("ACTION: do NOT add this to conversation history. Retry with higher max_tokens.")
    else:
        print("Stream complete:", collected)
except Exception as e:
    print(f"Stream error: {e} — retry original request")

# ── 5. Collect full text after stream ─────────────────────────────────────
print("\n=== 5. Get full text after stream ends ===")
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
