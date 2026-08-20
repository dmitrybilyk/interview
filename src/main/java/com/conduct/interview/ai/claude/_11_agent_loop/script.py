"""
Topic: Full agent loop — multiple tools, exit condition, human-in-the-loop
Cert notes section: "Building Production Agents"
  Agent = loop that calls tools, manages context, has a goal
  4 things every agent needs: tools, clear system prompt, tool results, exit condition
  Human-in-the-loop: before destructive actions, after plan, when tool result looks broken
Run: ../venv/bin/python script.py
"""

import os, json
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# ── Tool definitions ───────────────────────────────────────────────────────
TOOLS = [
    {
        "name": "search_products",
        "description": "Search the product catalog by keyword. Returns matching products with IDs and stock.",
        "input_schema": {
            "type": "object",
            "properties": {"query": {"type": "string", "description": "Search keyword"}},
            "required": ["query"]
        }
    },
    {
        "name": "check_stock",
        "description": "Check current stock level for a specific product ID.",
        "input_schema": {
            "type": "object",
            "properties": {"product_id": {"type": "string"}},
            "required": ["product_id"]
        }
    },
    {
        "name": "place_order",
        "description": "Place an order for a product. DESTRUCTIVE — use only after user confirms.",
        "input_schema": {
            "type": "object",
            "properties": {
                "product_id": {"type": "string"},
                "quantity": {"type": "integer", "minimum": 1}
            },
            "required": ["product_id", "quantity"]
        }
    },
    {
        "name": "task_complete",
        "description": "Call this when the task is fully done to end the agent loop.",
        "input_schema": {
            "type": "object",
            "properties": {"summary": {"type": "string", "description": "What was accomplished"}},
            "required": ["summary"]
        }
    }
]

# ── Tool implementations (your code — Claude never executes these) ─────────
CATALOG = {
    "PROD-001": {"name": "Laptop Pro 15", "price": 1299.99, "stock": 8},
    "PROD-002": {"name": "Wireless Mouse", "price": 29.99,  "stock": 45},
    "PROD-003": {"name": "Mechanical Keyboard", "price": 149.99, "stock": 12},
}
ORDERS = []

def search_products(query: str) -> str:
    results = [
        {"id": pid, "name": p["name"], "price": p["price"], "in_stock": p["stock"] > 0}
        for pid, p in CATALOG.items()
        if query.lower() in p["name"].lower()
    ]
    return json.dumps(results if results else {"message": "No products found"})

def check_stock(product_id: str) -> str:
    p = CATALOG.get(product_id)
    if not p:
        return json.dumps({"error": "Product not found"})
    return json.dumps({"product_id": product_id, "name": p["name"], "stock": p["stock"]})

def place_order(product_id: str, quantity: int) -> str:
    p = CATALOG.get(product_id)
    if not p:
        return json.dumps({"error": "Product not found"})
    if p["stock"] < quantity:
        return json.dumps({"error": f"Insufficient stock. Available: {p['stock']}"})
    p["stock"] -= quantity
    order_id = f"ORD-{len(ORDERS)+1:04d}"
    ORDERS.append({"id": order_id, "product_id": product_id, "quantity": quantity})
    return json.dumps({"order_id": order_id, "status": "confirmed", "quantity": quantity})

# ── Human-in-the-loop gate ─────────────────────────────────────────────────
DESTRUCTIVE_TOOLS = {"place_order"}

def human_approve(tool_name: str, inputs: dict) -> bool:
    """Pause and ask the human before any destructive tool call."""
    print(f"\n  ⚠  HUMAN APPROVAL REQUIRED")
    print(f"     Tool: {tool_name}")
    print(f"     Args: {json.dumps(inputs, indent=6)}")
    answer = input("     Approve? [y/N]: ").strip().lower()
    return answer == "y"

def execute_tool(name: str, inputs: dict) -> tuple[str, bool]:
    """Returns (result_str, was_blocked)."""
    if name in DESTRUCTIVE_TOOLS:
        if not human_approve(name, inputs):
            return json.dumps({"status": "cancelled", "reason": "User did not approve"}), True

    if name == "search_products":    return search_products(**inputs), False
    if name == "check_stock":        return check_stock(**inputs), False
    if name == "place_order":        return place_order(**inputs), False
    if name == "task_complete":      return json.dumps({"status": "done"}), False
    return json.dumps({"error": f"Unknown tool: {name}"}), False

# ── The agent loop ─────────────────────────────────────────────────────────
SYSTEM = """You are a shopping assistant agent. Help users find products and place orders.

Rules:
1. Always search for a product before checking stock or placing an order.
2. Always check stock before placing an order.
3. Call task_complete when your task is fully done.
4. Never place an order without confirming the product and stock first.
"""

def run_agent(user_request: str, max_iterations: int = 10):
    print(f"\n{'='*60}")
    print(f"User request: {user_request}")
    print('='*60)

    messages = [{"role": "user", "content": user_request}]
    iteration = 0

    while iteration < max_iterations:
        iteration += 1
        print(f"\n--- Agent iteration {iteration} ---")

        response = client.messages.create(
            model="claude-haiku-4-5-20251001",
            max_tokens=1024,
            system=SYSTEM,
            tools=TOOLS,
            messages=messages,
        )

        tool_use_blocks = [b for b in response.content if b.type == "tool_use"]
        text_blocks = [b for b in response.content if b.type == "text"]

        if text_blocks:
            for t in text_blocks:
                print(f"  [Claude thinks]: {t.text.strip()[:200]}")

        # Exit condition 1: end_turn with no tool calls
        if response.stop_reason == "end_turn" and not tool_use_blocks:
            print("\nAgent: task done (end_turn).")
            break

        # Exit condition 2: task_complete tool was called
        complete_blocks = [b for b in tool_use_blocks if b.name == "task_complete"]
        if complete_blocks:
            summary = complete_blocks[0].input.get("summary", "")
            print(f"\nAgent: task complete → {summary}")
            break

        if not tool_use_blocks:
            print("No tool calls and not end_turn — unexpected, stopping.")
            break

        # Add Claude's response to history
        messages.append({"role": "assistant", "content": response.content})

        # Execute all tool calls, collect results
        tool_results = []
        for block in tool_use_blocks:
            print(f"\n  [Tool call] {block.name}({json.dumps(block.input)})")
            result_str, blocked = execute_tool(block.name, block.input)
            print(f"  [Tool result] {result_str}")

            if blocked:
                result_str = json.dumps({"cancelled": True})

            tool_results.append({
                "type": "tool_result",
                "tool_use_id": block.id,
                "content": result_str,
            })

        messages.append({"role": "user", "content": tool_results})

    else:
        print(f"\nMax iterations ({max_iterations}) reached — stopping agent.")

# ── Run two scenarios ──────────────────────────────────────────────────────
run_agent("Find me a laptop and check if it's in stock.")

run_agent("I want to order 1 mechanical keyboard. Search for it, check stock, then order it.")
