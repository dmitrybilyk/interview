"""
Topic: Save an image to disk and load it back for a vision request
Cert notes section: "Images, PDFs and high-volume processing"
Run: ../venv/bin/python _3_image_from_disk.py
"""

import os, base64
from anthropic import Anthropic
from _common import make_test_png

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Load image from disk ===")
png_path = os.path.join(os.path.dirname(__file__), "test_image.png")
with open(png_path, "wb") as f:
    f.write(make_test_png(100, 60))

with open(png_path, "rb") as f:
    disk_b64 = base64.standard_b64encode(f.read()).decode("utf-8")

r3 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=64,
    messages=[{
        "role": "user",
        "content": [
            {"type": "image", "source": {"type": "base64", "media_type": "image/png", "data": disk_b64}},
            {"type": "text", "text": "What shape does this image appear to be — portrait or landscape?"}
        ]
    }]
)
print(r3.content[0].text.strip())
print(f"(Image saved to {png_path})")
