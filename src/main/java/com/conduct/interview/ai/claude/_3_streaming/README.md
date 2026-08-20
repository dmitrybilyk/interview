# _3 — Streaming

**Cert notes section:** Streaming, Context Management & RAG

## What this covers
| Concept | Cert note |
|---|---|
| Text streaming | Tokens arrive as they are generated |
| Event types | message_start, content_block_start, content_block_delta, message_stop |
| Incomplete stream | If stop not received — discard whole message, do NOT add to history |
| tool_use in streams | JSON chunks — must wait for closing block before parsing |

## How streaming works
```
API → message_start
    → content_block_start  (type: text)
    → content_block_delta  (text: "Hello")
    → content_block_delta  (text: " world")
    → content_block_stop
    → message_delta        (stop_reason, usage)
    → message_stop
```

## Key API
```python
with client.messages.stream(model=..., max_tokens=..., messages=[...]) as stream:
    for text in stream.text_stream:      # yields text chunks
        print(text, end="", flush=True)

final = stream.get_final_message()       # available after stream ends
final.stop_reason    # end_turn | max_tokens | tool_use
final.usage          # input + output tokens
```

## Incomplete stream rule (cert)
```python
if final.stop_reason == "max_tokens":
    # Stream was cut — discard collected text
    # Do NOT add this to conversation history
    # Retry original request with higher max_tokens
```

## What the script demonstrates
1. Basic text streaming with live output
2. Raw event type names
3. Stream with system prompt
4. Detecting and handling `max_tokens` cutoff
5. Collecting full text after stream ends

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _3_streaming && python script.py
```
