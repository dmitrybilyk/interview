# _12 — Full Agent Loop

An agent is a loop. It runs until a task is done, calling tools along the way.

```
send → Claude requests tool → you execute → send result → Claude continues
                                    ↑_________________________________|
                              repeat until stop_reason == end_turn
                              (or task_complete tool is called)
```

**4 things every agent needs:**
1. Tools defined (name + description + schema)
2. System prompt not vague — tell Claude the exact sequencing rules
3. Every `tool_use` block gets a `tool_result` back — no skipping
4. Exit condition — `task_complete` tool or `stop_reason == end_turn`

**Human-in-the-loop:** pause before destructive actions (delete, place_order, send_email). Ask for approval.

**Common mistake:** too many tools with overlapping descriptions → Claude picks the wrong one. Keep them precise and non-overlapping.

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: 4 tools, full loop with `max_iterations` guard, human approval gate — not runnable |
| `scenario_search_and_check_stock.py` | Read-only flow: search → check_stock → done |
| `scenario_order_with_approval.py` | Full flow: search → check_stock → human approval → place_order |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _12_agent_loop && ../venv/bin/python scenario_order_with_approval.py
# Prompts for approval before placing order — type "y" to confirm
```
