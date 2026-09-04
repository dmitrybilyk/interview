"""
Tool-use loop demo: get_account_balance
Run: python3 test_tools.py

BEFORE RUNNING:
1. Get a NEW API key at console.anthropic.com (revoke the one you pasted in chat!)
2. Set it as an environment variable, don't hardcode it in the file:
   export ANTHROPIC_API_KEY="sk-ant-your-new-key-here"
3. pip install anthropic
"""

import os
import json
from anthropic import Anthropic

client = Anthropic(api_key=os.environ.get("ANTHROPIC_API_KEY"))

# ---- STEP 1: Define schema ----
tools = [
    {
        "name": "get_account_balance",
        "description": (
            "Use this to retrieve the current balance for a specific account ID. "
            "Do not use this for transaction history."
        ),
        "input_schema": {
            "type": "object",
            "properties": {
                "account_id": {"type": "string", "description": "The account identifier, e.g. ACC-4471"}
            },
            "required": ["account_id"],
        },
    }
]

messages = [
    {"role": "user", "content": "What's the balance on account ACC-4471?"}
]

# ---- STEP 2: Send message ----
print("=== STEP 2: Sending initial message ===")
response = client.messages.create(
    model="claude-sonnet-5",
    max_tokens=1024,
    tools=tools,
    messages=messages,
)
print(json.dumps(response.model_dump(), indent=2, default=str))

# ---- STEP 3: Inspect tool_use block ----
tool_use_block = None
for block in response.content:
    if block.type == "tool_use":
        tool_use_block = block

if tool_use_block is None:
    print("\nClaude did not call a tool. Exiting.")
    exit()

print(f"\n=== STEP 3: Claude wants to call '{tool_use_block.name}' with input {tool_use_block.input} ===")

# ---- STEP 4: Execute tool (YOUR code, not Claude's) ----
def get_account_balance(account_id: str) -> str:
    # Fake DB lookup — replace with real logic
    fake_db = {"ACC-4471": "1245.00"}
    return fake_db.get(account_id, "Account not found")

account_id = tool_use_block.input["account_id"]
result = get_account_balance(account_id)
print(f"\n=== STEP 4: Executed locally, result = {result} ===")

# ---- STEP 5: Return result to Claude ----
messages.append({"role": "assistant", "content": response.content})
messages.append(
    {
        "role": "user",
        "content": [
            {
                "type": "tool_result",
                "tool_use_id": tool_use_block.id,  # MUST match exactly
                "content": result,
            }
        ],
    }
)

print("\n=== STEP 5: Sending tool_result back ===")

# ---- STEP 6: Claude continues ----
final_response = client.messages.create(
    model="claude-sonnet-5",
    max_tokens=1024,
    tools=tools,
    messages=messages,
)

print("\n=== STEP 6: Claude's final answer ===")
for block in final_response.content:
    if block.type == "text":
        print(block.text)