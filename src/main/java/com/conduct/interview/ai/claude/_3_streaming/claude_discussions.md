# Discussion topics — _3_streaming

- What is the difference between `stream=True` and using `client.messages.stream()`? Are they the same under the hood?
- `raw_event_stream.py` — what are all the event types in an SSE stream? What does `message_delta` carry vs `content_block_delta`?
- If the network drops mid-stream, what does the SDK throw? How do you resume or retry a partial stream?
- `incomplete_stream_handling.py` — how do you know if the stream ended cleanly vs was cut? Is finish_reason available before the stream closes?
- `collect_full_text_after_stream.py` — why would you stream and then collect, instead of just not streaming? What's the UX use case?
- Streaming vs non-streaming: is the total token count the same? Is latency to first token always better with streaming?
- Can you use tool_use with streaming? What does the event sequence look like when a tool call is in the stream?
- `streaming_with_system_prompt.py` — does the system prompt affect streaming behavior, or only the content?
- Backpressure: if your consumer is slow and the stream produces events faster, what happens? Does the SDK buffer?
- What's the difference between `input_tokens` reported at stream start vs `output_tokens` reported at stream end? When does each become available?
