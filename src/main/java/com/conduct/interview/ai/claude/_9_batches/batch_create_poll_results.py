"""
Topic: Message Batches API — create, poll until done, iterate results
Cert notes section: "Async patterns for high-volume work — Message Batches API"
Run: ../venv/bin/python batch_create_poll_results.py
Note: batch processing is async — the script polls until done (may take 1-5 min)
"""

import os, time
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Batches are best for: bulk classification, offline enrichment, non-real-time jobs.
# You pay the same token price but get up to 50% discount on some plans.
# A batch can hold up to 100,000 requests.

# ── 1. Create a batch of requests ─────────────────────────────────────────
print("=== 1. Creating batch ===")

requests = [
    {
        "custom_id": "sentiment-1",
        "params": {
            "model": "claude-haiku-4-5-20251001",
            "max_tokens": 16,
            "messages": [{"role": "user", "content": "Sentiment (pos/neg/neutral only): 'Best product ever!'"}]
        }
    },
    {
        "custom_id": "sentiment-2",
        "params": {
            "model": "claude-haiku-4-5-20251001",
            "max_tokens": 16,
            "messages": [{"role": "user", "content": "Sentiment (pos/neg/neutral only): 'Arrived broken, terrible.'"}]
        }
    },
    {
        "custom_id": "sentiment-3",
        "params": {
            "model": "claude-haiku-4-5-20251001",
            "max_tokens": 16,
            "messages": [{"role": "user", "content": "Sentiment (pos/neg/neutral only): 'It is okay, nothing special.'"}]
        }
    },
    {
        "custom_id": "capital-1",
        "params": {
            "model": "claude-haiku-4-5-20251001",
            "max_tokens": 16,
            "messages": [{"role": "user", "content": "Capital of Japan? One word only."}]
        }
    },
    {
        "custom_id": "capital-2",
        "params": {
            "model": "claude-haiku-4-5-20251001",
            "max_tokens": 16,
            "messages": [{"role": "user", "content": "Capital of Brazil? One word only."}]
        }
    },
]

batch = client.messages.batches.create(requests=requests)
print(f"  Batch ID: {batch.id}")
print(f"  Status:   {batch.processing_status}")
print(f"  Requests: {batch.request_counts.processing} processing")

# ── 2. Poll until processing is done ──────────────────────────────────────
print("\n=== 2. Polling for completion ===")
POLL_INTERVAL = 10   # seconds

while True:
    batch = client.messages.batches.retrieve(batch.id)
    counts = batch.request_counts
    print(f"  [{time.strftime('%H:%M:%S')}] status={batch.processing_status}  "
          f"processing={counts.processing}  succeeded={counts.succeeded}  errored={counts.errored}")

    if batch.processing_status == "ended":
        break

    time.sleep(POLL_INTERVAL)

print(f"\nBatch ended. Total: {batch.request_counts.succeeded} succeeded, "
      f"{batch.request_counts.errored} errored.")

# ── 3. Iterate results ────────────────────────────────────────────────────
print("\n=== 3. Results ===")
for result in client.messages.batches.results(batch.id):
    cid = result.custom_id
    if result.result.type == "succeeded":
        text = result.result.message.content[0].text.strip()
        tokens = result.result.message.usage
        print(f"  [{cid}] '{text}'  (in={tokens.input_tokens} out={tokens.output_tokens})")
    elif result.result.type == "errored":
        print(f"  [{cid}] ERROR: {result.result.error}")

print("""
KEY TAKEAWAYS (cert notes):
- Batches are fire-and-forget — submit and poll, don't block
- Use for: bulk classification, offline enrichment, report generation
- Don't use for: real-time user responses (use synchronous or streaming)
- Results are available for 29 days after the batch ends
- custom_id lets you match results back to your original requests
""")
