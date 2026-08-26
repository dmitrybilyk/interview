"""
Topic: Tool use — two tool calls requested in a single turn
Cert notes section: Tool use — "Claude never runs code, it requests a tool_use block"
Run: ../venv/bin/python _2_two_tool_calls.py
"""

from _common import run_with_tools

print("=== Example: Two tool calls in one turn ===")
run_with_tools("Compare the weather in Paris and Tokyo in fahrenheit.")
