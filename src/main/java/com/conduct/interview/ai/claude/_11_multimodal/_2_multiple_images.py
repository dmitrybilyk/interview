"""
Topic: Send multiple images in a single message
Cert notes section: "Images, PDFs and high-volume processing"
Run: ../venv/bin/python _2_multiple_images.py
"""

import os, base64
from anthropic import Anthropic
from _common import make_test_png

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Multiple images ===")
png1 = make_test_png(60, 40)
png2 = make_test_png(80, 80)   # different size

r2 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    messages=[{
        "role": "user",
        "content": [
            {"type": "text", "text": "I have two images:"},
            {"type": "image", "source": {"type": "base64", "media_type": "image/png",
                                          "data": base64.standard_b64encode(png1).decode()}},
            {"type": "text", "text": "and"},
            {"type": "image", "source": {"type": "base64", "media_type": "image/png",
                                          "data": base64.standard_b64encode(png2).decode()}},
            {"type": "text", "text": "Do both images have a gradient? Reply yes or no."},
        ]
    }]
)
print(r2.content[0].text.strip())
