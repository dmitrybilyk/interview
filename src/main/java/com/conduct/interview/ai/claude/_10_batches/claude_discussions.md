# Discussion topics — _9_batches

- `batch_create_poll_results.py` — batches are async and results come later. What's the typical latency: minutes, hours? What's the SLA?
- Batch API costs 50% less than synchronous. What's the exact pricing model and when does the saving justify the async complexity?
- `list_batches.py` — you can list all batches. How long are results retained before the API deletes them?
- What's the maximum batch size (number of requests)? What happens if you exceed it — error at creation time or partial processing?
- Each request in a batch is independent — if one fails, do the others still complete? How do you identify which ones failed?
- Can you cancel a batch mid-processing? What happens to already-processed items?
- Batches don't support streaming — is there a way to get partial results as the batch processes, or only after completion?
- If a batch request hits the rate limit internally, does the API retry automatically or mark it as failed?
- How do you correlate batch results back to your original requests? What identifier do you use?
- What use cases are clearly NOT suited for batches (need real-time response, interactive user, short deadline)?
