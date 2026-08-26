# _5 — Tool Use

**Cert notes section:** Tool use — "Claude never runs code, it requests a tool_use block (name + args) as JSON, my app executes, sends back tool_result, Claude continues."

## The loop — 6 steps
```
1. You define tool schemas
2. Send user message with tools list
3. Claude replies with stop_reason=tool_use + tool_use block(s)
4. YOU execute the tool (your code, not Claude's)
5. Send tool_result back in a new user message
6. Claude reads result, continues or calls another tool
```

## Key structures
```python
# Tool schema
{
    "name": "get_weather",
    "description": "...",          # Claude decides to call based on this
    "input_schema": {
        "type": "object",
        "properties": {"city": {"type": "string"}},
        "required": ["city"]
    }
}

# Tool result (MUST use tool_use_id from Claude's response)
{
    "type": "tool_result",
    "tool_use_id": block.id,       # ← must match exactly
    "content": json.dumps(result)  # string
}
```

## stop_reason values
| stop_reason | Meaning |
|---|---|
| `tool_use` | Claude wants to call tool(s) — execute and send results back |
| `end_turn` | Claude is done — read the text answer |
| `max_tokens` | Cut short — discard, retry |

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: tool definitions (weather + population), `execute_tool`, `run_with_tools` loop — not runnable on its own |
| `single_tool_call.py` | Claude calling a single tool |
| `two_tool_calls.py` | Claude calling two tools in one turn (parallel tool calls) |
| `no_tool_needed.py` | Claude deciding no tool is needed (end_turn directly) |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _5_tool_use && python single_tool_call.py   # or any other scenario file
```
