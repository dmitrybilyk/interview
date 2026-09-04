"""
Topic: Tool use — Claude decides no tool is needed and answers directly
Cert notes section: Tool use — "Claude never runs code, it requests a tool_use block"
Run: ../venv/bin/python _3_no_tool_needed.py
"""

from _common import run_with_tools

print("=== Example: Claude decides no tool is needed ===")
run_with_tools("What is 2 + 2?")
