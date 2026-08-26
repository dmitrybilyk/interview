# _12 — Full Agent Loop

**Cert notes section:** "Building Production Agents"  
"Agent is a loop that calls tools, manages context, and has a goal"  
"Every Agent loop needs 4 things: tools registered, system prompt not vague, every tool gets a result, exit condition defined"

## What this covers
| Concept | Detail |
|---|---|
| Agent loop | Iterate: send → detect tool_use → execute → send result → repeat |
| Exit condition | `task_complete` tool or `stop_reason == end_turn` |
| Human-in-the-loop | Pause before destructive actions, ask for approval |
| max_iterations | Hard cap to prevent infinite loops |
| System prompt | Non-vague: must define sequencing rules explicitly |

## Agent vs Workflow (cert note)
- **Workflow** — you can list the exact steps in advance
- **Agent** — steps depend on input and are not predictable → needs a loop

## The 4 required things (cert)
```python
# 1. Tools registered
tools = [{"name": ..., "description": ..., "input_schema": ...}]

# 2. System prompt is NOT vague
system = "Always search before checking stock. Always check stock before ordering."

# 3. Every tool call gets a result back
tool_results.append({"type": "tool_result", "tool_use_id": block.id, "content": result})

# 4. Exit condition defined
# Option A: task_complete tool
# Option B: stop_reason == "end_turn" with no tool calls
```

## Human-in-the-loop gates (cert note)
- Before anything destructive (delete, place_order, send_email)
- After Claude makes a plan (review before executing)
- When tool result looks broken (unexpected null / error)

## Common mistake (cert note)
> Too many tools — Claude gets confused about when to use which one.  
> Keep descriptions precise and non-overlapping.

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: 4 tools (`search_products`, `check_stock`, `place_order`, `task_complete`), the full loop with iteration counter + max_iterations guard, and the human approval gate before `place_order` — not runnable on its own |
| `scenario_search_and_check_stock.py` | Read-only flow: search → check_stock → task_complete, no approval needed |
| `scenario_order_with_approval.py` | Full order flow: search → check_stock → (ask human) → place_order → task_complete |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _12_agent_loop && python scenario_order_with_approval.py
# Prompts for approval before placing an order — type "y" to confirm
```
