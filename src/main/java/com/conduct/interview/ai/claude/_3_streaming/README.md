# _3 — Streaming

Same as a normal call but tokens arrive as chunks instead of all at once.

If `stop_reason == "max_tokens"` → stream was cut mid-sentence → **discard it, do NOT add to history**, retry with higher `max_tokens`.

```python
with client.messages.stream(model=..., max_tokens=..., messages=[...]) as stream:
    for text in stream.text_stream:
        print(text, end="", flush=True)

final = stream.get_final_message()
final.stop_reason   # end_turn | max_tokens | tool_use
final.usage
```

Event sequence under the hood: `message_start` → `content_block_start` → `content_block_delta` × N → `content_block_stop` → `message_delta` → `message_stop`

## Scripts
| File | Demonstrates |
|---|---|
| `text_streaming.py` | Basic streaming with live output |
| `raw_event_stream.py` | Raw event type names |
| `streaming_with_system_prompt.py` | Stream with system prompt |
| `incomplete_stream_handling.py` | Detecting `max_tokens` cutoff |
| `collect_full_text_after_stream.py` | Collecting full text after stream ends |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _3_streaming && ../venv/bin/python text_streaming.py
```
