# _5 — Tool Use

Claude never runs code. It asks you to run it.

1. Claude sees a question + your tool definitions
2. Claude replies with `stop_reason=tool_use` + a `tool_use` block (name + args)
3. **Your code** executes the tool
4. You send `tool_result` back (matched by `tool_use_id`)
5. Claude reads the result and continues — or calls another tool

```python
# Tool result you send back:
{"type": "tool_result", "tool_use_id": block.id, "content": json.dumps(result)}

# Loop until:
response.stop_reason == "end_turn"   # Claude is done, read the text answer
```

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: tool definitions, `execute_tool`, `run_with_tools` loop — not runnable |
| `single_tool_call.py` | Claude calling one tool |
| `two_tool_calls.py` | Claude calling two tools in parallel |
| `no_tool_needed.py` | Claude answering without calling any tool |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _5_tool_use && ../venv/bin/python single_tool_call.py
```
