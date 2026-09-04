"""
Topic: packaging an MCP server — write down what each tool needs, let
       whoever installs it pick which tools are turned on
Cert notes section: "Packaging for reuse"
Run:
    python3 _2_document_mcp_server_manifest.py
    python3 _2_document_mcp_server_manifest.py --scope read_only

You need ANTHROPIC_API_KEY set even though this script never calls the
API. It borrows the tool list (TOOLS) from _12_agent_loop/_common.py, and
just importing that file also builds an Anthropic client as a side effect.
That's exactly the kind of hidden dependency worth writing down when you
document something: a cleanly packaged server would let you read the tool
list without needing an API key at all. See claude_discussions.md.
"""
import json
import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(__file__), "..", "_12_agent_loop"))
from _common import TOOLS  # noqa: E402 — reusable core: tool schemas, untouched

SCOPES = {
    "read_only": {"search_products", "check_stock"},
    "read_write": {"search_products", "check_stock", "place_order", "task_complete"},
}


def build_manifest(scope: str) -> dict:
    """What an installing team gets on install: every tool's name,
    description and input schema, plus which ones this scope actually
    turns on. Going from a read-only demo install to a full one is a
    --scope flag, not a code change."""
    if scope not in SCOPES:
        raise ValueError(f"Unknown scope '{scope}'. Choices: {sorted(SCOPES)}")
    allowed = SCOPES[scope]
    return {
        "server": "shopping-tools",
        "scope": scope,
        "tools": [
            {
                "name": t["name"],
                "description": t["description"],
                "input_schema": t["input_schema"],
                "enabled": t["name"] in allowed,
            }
            for t in TOOLS
        ],
    }


if __name__ == "__main__":
    chosen_scope = "read_write"
    if "--scope" in sys.argv:
        chosen_scope = sys.argv[sys.argv.index("--scope") + 1]
    print(json.dumps(build_manifest(chosen_scope), indent=2))
