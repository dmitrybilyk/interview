"""
Topic: List recent batches — useful for monitoring
Cert notes section: "Async patterns for high-volume work — Message Batches API"
Run: ../venv/bin/python list_batches.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Recent batches ===")
for b in client.messages.batches.list(limit=3):
    print(f"  {b.id}  status={b.processing_status}  "
          f"created={b.created_at.strftime('%Y-%m-%d %H:%M')}")
