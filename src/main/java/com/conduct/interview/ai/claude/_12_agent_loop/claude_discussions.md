# Discussion topics — _11_agent_loop

- `scenario_order_with_approval.py` — the agent pauses for human approval before acting. How do you implement the pause: poll, webhook, or human-in-the-loop UI?
- `scenario_search_and_check_stock.py` — the agent calls tools in sequence based on intermediate results. How many tool-call iterations is "too many" before you should break the loop?
- What's the termination condition for an agent loop? How do you detect the model is "done" vs stuck in a tool-calling loop?
- `_common.py` — what shared setup is needed across agent scenarios? Tool registry, session state, logging?
- Agent loops accumulate the full tool-call history in the `messages` array. At what depth does context become a practical problem?
- How do you handle a tool that takes a long time (e.g., external API that takes 30s)? Timeout, retry, or async?
- `scenario_order_with_approval.py` — if the human rejects the action, how does the agent recover? Does it re-plan or just stop?
- What's the difference between this manual agent loop and using an SDK-level agent abstraction (like LangChain, or Anthropic's own Managed Agents)?
- Security: in an agent loop, the model constructs tool arguments. How do you validate or sanitize those arguments before executing (e.g., SQL injection via a `query` tool)?
- How do you add memory to an agent loop — storing facts across separate sessions — without growing the context window indefinitely?
