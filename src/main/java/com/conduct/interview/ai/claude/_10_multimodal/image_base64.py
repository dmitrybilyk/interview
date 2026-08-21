"""
Topic: Send an image to Claude via base64 encoding
Cert notes section: "Images, PDFs and high-volume processing"
Run: ../venv/bin/python image_base64.py
"""

import os, base64
from anthropic import Anthropic
from _common import make_test_png

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Image via base64 ===")
png_bytes = make_test_png()
image_b64 = base64.standard_b64encode(png_bytes).decode("utf-8")

r1 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    messages=[{
        "role": "user",
        "content": [
            {
                "type": "image",
                "source": {
                    "type": "base64",
                    "media_type": "image/png",
                    "data": image_b64,
                }
            },
            {
                "type": "text",
                "text": "Describe the dominant colors in this image in one sentence."
            }
        ]
    }]
)
print(r1.content[0].text.strip())
print(f"Tokens: in={r1.usage.input_tokens}  out={r1.usage.output_tokens}")
