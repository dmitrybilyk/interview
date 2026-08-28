# _10 — Message Batches API

Submit up to 100k requests at once, get a batch ID, poll later. ~50% cheaper than sync. No real-time response.

```python
# Submit
batch = client.messages.batches.create(requests=[
    {"custom_id": "req-1", "params": {"model": ..., "max_tokens": ..., "messages": [...]}}
])

# Poll until done
batch = client.messages.batches.retrieve(batch.id)
batch.processing_status   # "in_progress" | "ended"

# Read results
for result in client.messages.batches.results(batch.id):
    result.custom_id
    result.result.type      # "succeeded" | "errored"
    result.result.message   # MessageResponse if succeeded
```

Use for: bulk classification, offline enrichment, report generation.  
Don't use for: real-time chat, anything needing sub-second latency.

## Scripts
| File | Demonstrates |
|---|---|
| `batch_create_poll_results.py` | Create 5-request batch, poll every 10s, print results |
| `list_batches.py` | List recent batches |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _10_batches && ../venv/bin/python batch_create_poll_results.py
# Expect 1-5 min wait for batch to complete
```
