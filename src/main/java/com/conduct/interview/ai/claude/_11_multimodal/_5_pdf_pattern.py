"""
Topic: PDF pattern — same content-block mechanism as images, document type instead
Cert notes section: "PDF is a document, treated the same as image file"
Run: ../venv/bin/python _5_pdf_pattern.py
Note: this file only prints the pattern — it needs a real PDF file to execute.
"""

print("=== PDF pattern (code shown, not executed) ===")
print("""
# PDF is sent as a document block, not an image block:

import base64
from anthropic import Anthropic

client = Anthropic()

with open("document.pdf", "rb") as f:
    pdf_b64 = base64.standard_b64encode(f.read()).decode()

response = client.messages.create(
    model="claude-sonnet-4-6",
    max_tokens=1024,
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
            {"type": "text", "text": "Summarize this document in 3 bullet points."}
        ]
    }]
)
""")
