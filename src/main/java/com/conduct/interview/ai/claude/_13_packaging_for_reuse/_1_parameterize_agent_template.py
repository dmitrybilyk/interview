"""
Topic: packaging an agent as a template — same _12 shopping agent, but
       driven by a config file instead of hardcoded values
Cert notes section: "Packaging for reuse"
Run:
    python3 _1_parameterize_agent_template.py
    python3 _1_parameterize_agent_template.py acme_customer.example.json

What's different from _12_agent_loop/_common.py: the model name, how many
steps the agent can take, which tools need approval before running, and
any extra rules for the system prompt used to be hardcoded — now they come
from config.default.json (with sensible defaults written down) plus an
optional file for one customer's overrides. The tool definitions and the
actual tool code are untouched, imported straight from _12.
"""
import json
import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(__file__), "..", "_12_agent_loop"))
from _common import (  # noqa: E402 — reusable core, imported after sys.path fix
    TOOLS,
    check_stock,
    client,
    human_approve,
    place_order,
    search_products,
)

from _pkg import audit_log, load_config  # this folder's helpers (named _pkg, not
                                          # _common — _12_agent_loop already owns
                                          # that module name and Python's import
                                          # cache would collide with it otherwise)

BASE_SYSTEM = """You are a shopping assistant agent. Help users find products and place orders.

Rules:
1. Always search for a product before checking stock or placing an order.
2. Always check stock before placing an order.
3. Call task_complete when your task is fully done.
4. Never place an order without confirming the product and stock first.
5. Do NOT ask the user for confirmation in plain text before calling place_order.
   The system already pauses for human approval automatically before that tool
   executes — just call place_order directly once stock is confirmed.
"""


def execute_tool(name: str, inputs: dict, config: dict):
    """Approval gate driven by config — which tools are destructive and
    whether approval is required at all are parameters, not a hardcoded set."""
    if name in config["destructive_tools"] and config["require_approval"]:
        if not human_approve(name, inputs):
            audit_log("tool_blocked", config["identity"], {"tool": name, "inputs": inputs})
            return json.dumps({"status": "cancelled", "reason": "User did not approve"}), True

    if name == "search_products":
        result = search_products(**inputs)
    elif name == "check_stock":
        result = check_stock(**inputs)
    elif name == "place_order":
        result = place_order(**inputs)
    elif name == "task_complete":
        result = json.dumps({"status": "done"})
    else:
        result = json.dumps({"error": f"Unknown tool: {name}"})

    if "_trace" in config:  # only set when _3_eval_gate.py drives this run
        config["_trace"].append(name)

    audit_log("tool_executed", config["identity"], {"tool": name, "inputs": inputs})
    return result, False


def run_agent(user_request: str, config: dict):
    system = BASE_SYSTEM
    if config["system_prompt_extra"]:
        system += "\n" + config["system_prompt_extra"]

    print(f"\n{'=' * 60}\nUser request: {user_request}\n{'=' * 60}")
    messages = [{"role": "user", "content": user_request}]

    for iteration in range(1, config["max_iterations"] + 1):
        print(f"\n--- Agent iteration {iteration} ---")
        response = client.messages.create(
            model=config["model"],
            max_tokens=1024,
            system=system,
            tools=TOOLS,
            messages=messages,
        )

        tool_use_blocks = [b for b in response.content if b.type == "tool_use"]
        for t in (b for b in response.content if b.type == "text"):
            print(f"  [Claude thinks]: {t.text.strip()[:200]}")

        if response.stop_reason == "end_turn" and not tool_use_blocks:
            print("\nAgent: task done (end_turn).")
            return
        complete = [b for b in tool_use_blocks if b.name == "task_complete"]
        if complete:
            print(f"\nAgent: task complete -> {complete[0].input.get('summary', '')}")
            return
        if not tool_use_blocks:
            print("No tool calls and not end_turn — unexpected, stopping.")
            return

        messages.append({"role": "assistant", "content": response.content})
        tool_results = []
        for block in tool_use_blocks:
            print(f"\n  [Tool call] {block.name}({json.dumps(block.input)})")
            result, blocked = execute_tool(block.name, block.input, config)
            print(f"  [Tool result] {result}")
            if blocked:
                result = json.dumps({"cancelled": True})
            tool_results.append({"type": "tool_result", "tool_use_id": block.id, "content": result})
        messages.append({"role": "user", "content": tool_results})

    print(f"\nMax iterations ({config['max_iterations']}) reached — stopping agent.")


if __name__ == "__main__":
    override_path = sys.argv[1] if len(sys.argv) > 1 else None
    cfg = load_config(override_path)
    run_agent(
        "I want to order 1 mechanical keyboard. Search for it, check stock, then order it.",
        cfg,
    )
