"""
Topic: PDF pattern — same content-block mechanism as images, document type instead
Cert notes section: "PDF is a document, treated the same as image file"
Run: ../venv/bin/python _5_pdf_pattern.py
"""

import os, base64
from anthropic import Anthropic
from _common import make_test_pdf

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== PDF via document content block ===")
pdf_bytes = make_test_pdf("Hello Claude, this is a test PDF.")
pdf_b64 = base64.standard_b64encode(pdf_bytes).decode("utf-8")

r5 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    messages=[{
        "role": "user",
        "content": [
            {
                "type": "document",
                "source": {
                    "type": "base64",
                    "media_type": "application/pdf",
                    "data": pdf_b64,
                }
            },
            {"type": "text", "text": "What text is written in this PDF? Quote it exactly."}
        ]
    }]
)
print(r5.content[0].text.strip())
print(f"Tokens: in={r5.usage.input_tokens}  out={r5.usage.output_tokens}")
