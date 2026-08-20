"""
Topic: Tool use — define schema, detect tool_use block, execute, return tool_result
Cert notes section: Tool use — "Claude never runs code, it requests a tool_use block"
Run: ../venv/bin/python script.py
"""

import os, json
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# ── Step 1: Define tool schemas ────────────────────────────────────────────
# Claude reads these to know what tools exist and when to call them.
# The description is critical — Claude decides based on it.
TOOLS = [
    {
        "name": "get_weather",
        "description": "Returns current weather for a city. Use this when the user asks about weather.",
        "input_schema": {
            "type": "object",
            "properties": {
                "city": {"type": "string", "description": "City name, e.g. 'Berlin'"},
                "unit": {"type": "string", "enum": ["celsius", "fahrenheit"], "description": "Temperature unit"}
            },
            "required": ["city"]
        }
    },
    {
        "name": "get_population",
        "description": "Returns the population of a city. Use when the user asks about city population.",
        "input_schema": {
            "type": "object",
            "properties": {
                "city": {"type": "string", "description": "City name"}
            },
            "required": ["city"]
        }
    }
]

# ── Step 2: YOUR execution functions — Claude never runs these ─────────────
def get_weather(city: str, unit: str = "celsius") -> dict:
    fake_db = {
        "berlin":  {"temp": 18, "condition": "cloudy"},
        "paris":   {"temp": 22, "condition": "sunny"},
        "tokyo":   {"temp": 28, "condition": "humid"},
    }
    data = fake_db.get(city.lower(), {"temp": 20, "condition": "unknown"})
    symbol = "°C" if unit == "celsius" else "°F"
    temp = data["temp"] if unit == "celsius" else int(data["temp"] * 9/5 + 32)
    return {"city": city, "temperature": f"{temp}{symbol}", "condition": data["condition"]}

def get_population(city: str) -> dict:
    fake_db = {"berlin": 3_645_000, "paris": 2_161_000, "tokyo": 13_960_000}
    return {"city": city, "population": fake_db.get(city.lower(), "unknown")}

def execute_tool(name: str, inputs: dict) -> str:
    if name == "get_weather":
        return json.dumps(get_weather(**inputs))
    if name == "get_population":
        return json.dumps(get_population(**inputs))
    return json.dumps({"error": f"Unknown tool: {name}"})

# ── Step 3: The tool loop ─────────────────────────────────────────────────
def run_with_tools(user_message: str):
    print(f"\nUser: {user_message}")
    messages = [{"role": "user", "content": user_message}]

    while True:
        response = client.messages.create(
            model="claude-haiku-4-5-20251001",
            max_tokens=1024,
            tools=TOOLS,
            messages=messages,
        )

        print(f"\nStop reason: {response.stop_reason}")

        # Collect all tool_use blocks in this response
        tool_use_blocks = [b for b in response.content if b.type == "tool_use"]

        if response.stop_reason == "end_turn" or not tool_use_blocks:
            # Claude is done — extract final text
            for block in response.content:
                if block.type == "text":
                    print(f"\nClaude: {block.text}")
            break

        # stop_reason == "tool_use" — Claude wants one or more tools executed
        # Add Claude's response (with tool_use blocks) to history
        messages.append({"role": "assistant", "content": response.content})

        # Execute all requested tools and collect results
        tool_results = []
        for block in tool_use_blocks:
            print(f"\n  [Tool call] {block.name}({block.input})")
            result = execute_tool(block.name, block.input)
            print(f"  [Tool result] {result}")
            tool_results.append({
                "type": "tool_result",
                "tool_use_id": block.id,    # MUST match block.id exactly
                "content": result,
            })

        # Return all results in a single user message
        messages.append({"role": "user", "content": tool_results})

# ── Run examples ──────────────────────────────────────────────────────────
print("=== Example 1: Single tool call ===")
run_with_tools("What's the weather in Berlin?")

print("\n" + "="*50)
print("=== Example 2: Two tool calls in one turn ===")
run_with_tools("Compare the weather in Paris and Tokyo in fahrenheit.")

print("\n" + "="*50)
print("=== Example 3: Claude decides no tool is needed ===")
run_with_tools("What is 2 + 2?")
