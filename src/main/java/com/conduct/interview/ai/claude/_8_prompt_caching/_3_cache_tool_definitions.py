"""
Topic: Prompt caching — caching the tool list (same cache_control pattern)
Cert notes section: "2 API features reduce what you pay: Prompt caching, Token counting"
Run: ../venv/bin/python _3_cache_tool_definitions.py
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

print("=== Caching tool list (same pattern) ===")
tools = [
    {
        "name": "query_db",
        "description": "Query the database for records.",
        "input_schema": {"type": "object", "properties": {"sql": {"type": "string"}}, "required": ["sql"]},
        "cache_control": {"type": "ephemeral"}   # cache_control on the LAST tool
    }
]
r4 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=64,
    tools=tools,
    messages=[{"role": "user", "content": "What tools do you have?"}],
)
print(f"  Tool list cached. cache_creation: {getattr(r4.usage, 'cache_creation_input_tokens', 0)}")

print("""
KEY TAKEAWAYS (cert notes):
- Cache the static parts: system prompt, tool list, large documents
- cache_control goes on the LAST block you want cached (e.g. last tool in the list)
""")
