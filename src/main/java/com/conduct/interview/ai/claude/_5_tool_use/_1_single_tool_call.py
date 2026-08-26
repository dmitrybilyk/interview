"""
Topic: Tool use — a single tool call in one turn
Cert notes section: Tool use — "Claude never runs code, it requests a tool_use block"
Run: ../venv/bin/python _1_single_tool_call.py
"""

from _common import run_with_tools

print("=== Example: Single tool call ===")
run_with_tools("What's the weather in Berlin?")
