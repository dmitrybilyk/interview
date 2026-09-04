"""
Topic: Tool definitions add tokens too — measure the overhead with count_tokens
Cert notes section: "2 API features reduce what you pay" + "What affects budget"
Run: ../venv/bin/python _3_tools_cost.py
"""

from _common import client

print("=== Tools add tokens too ===")
model = "claude-haiku-4-5-20251001"
messages = [{"role": "user", "content": "What is the capital of France?"}]

result_no_tools = client.messages.count_tokens(model=model, messages=messages)

tools = [
    {
        "name": "search_web",
        "description": "Search the web for up-to-date information on any topic.",
        "input_schema": {
            "type": "object",
            "properties": {"query": {"type": "string", "description": "Search query"}},
            "required": ["query"]
        }
    },
    {
        "name": "run_sql",
        "description": "Execute a read-only SQL query against the database.",
        "input_schema": {
            "type": "object",
            "properties": {"sql": {"type": "string"}},
            "required": ["sql"]
        }
    }
]
result_with_tools = client.messages.count_tokens(model=model, tools=tools, messages=messages)
print(f"  Without tools: {result_no_tools.input_tokens} tokens")
print(f"  With 2 tools:  {result_with_tools.input_tokens} tokens")
print(f"  Tools overhead: {result_with_tools.input_tokens - result_no_tools.input_tokens} tokens")
