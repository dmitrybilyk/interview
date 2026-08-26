# Discussion topics — _4_tool_use

- `single_tool_call.py` — what does the response look like when the model decides to call a tool? What fields does `content[0]` have vs a normal text response?
- After calling a tool you must send the result back — what exactly goes in the `tool_result` message? What role does it have: user or assistant?
- `no_tool_needed.py` — how does the model decide whether to call a tool or answer directly? Can you force it either way?
- `two_tool_calls.py` — can the model call two tools in one response (parallel tool use)? How does the response structure differ from sequential calls?
- What happens if your tool function raises an exception — how do you pass an error back to the model as a tool result?
- Tool definitions are sent as part of input tokens — how many tokens does a typical tool schema cost? Does schema complexity matter?
- `tool_choice` parameter — what are the options (auto, any, specific tool)? When would you force a specific tool?
- Can the model call the same tool multiple times in one conversation turn?
- Security concern: user input goes into tool arguments — what prevents prompt injection via tool parameters?
- How do you build a tool that calls another Claude API (agent-to-agent)? What does that nesting look like?
