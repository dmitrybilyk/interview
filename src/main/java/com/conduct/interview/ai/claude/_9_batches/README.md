# _9 — Message Batches API

**Cert notes section:** "Async patterns for high-volume work — Message Batches API"

## What this covers
| Concept | Detail |
|---|---|
| Fire-and-forget | Submit batch, get ID, poll later |
| `custom_id` | Your ID to match results back to requests |
| Polling | Check `processing_status` until `"ended"` |
| Results | Iterate via `client.messages.batches.results(batch_id)` |
| Limits | Up to 100,000 requests per batch |

## When to use batches
| Use batch | Use sync/stream |
|---|---|
| Bulk classification (1000s of records) | Real-time user responses |
| Overnight report generation | Interactive chat |
| Offline enrichment pipelines | Sub-second latency needed |

## Key API
```python
# Create
batch = client.messages.batches.create(requests=[
    {"custom_id": "my-id-1", "params": {"model": ..., "max_tokens": ..., "messages": [...]}}
])

# Poll
batch = client.messages.batches.retrieve(batch.id)
batch.processing_status  # "in_progress" | "ended"
batch.request_counts     # .processing, .succeeded, .errored

# Results (after "ended")
for result in client.messages.batches.results(batch.id):
    result.custom_id
    result.result.type        # "succeeded" | "errored"
    result.result.message     # MessageResponse (if succeeded)
```

## Result types
```python
if result.result.type == "succeeded":
    text = result.result.message.content[0].text
elif result.result.type == "errored":
    error = result.result.error
```

## What the script demonstrates
1. Create a batch of 5 requests (3 sentiment + 2 capital lookups)
2. Poll every 10 seconds until `processing_status == "ended"`
3. Iterate and print all results
4. List recent batches

**Note:** the script blocks while polling — in production this would be a background job.

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _9_batches && python script.py
# Expect to wait 1-5 minutes for the batch to complete
```
