"""
Topic: Basic text streaming — tokens arrive as they are generated
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python text_streaming.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Text streaming ===")
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
