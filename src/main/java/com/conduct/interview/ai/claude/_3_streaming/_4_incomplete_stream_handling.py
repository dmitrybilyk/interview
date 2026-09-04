"""
Topic: Incomplete stream — check stop_reason before trusting the text
Cert note: "If looping is done but message stop is not received — discard the whole message"
Run: ../venv/bin/python _4_incomplete_stream_handling.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Incomplete stream — stop_reason check ===")
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
