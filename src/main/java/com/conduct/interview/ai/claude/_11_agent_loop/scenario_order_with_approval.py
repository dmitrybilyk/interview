"""
Topic: Agent loop — destructive scenario (place_order gated by human approval)
Cert notes section: "Building Production Agents" — human-in-the-loop before destructive actions
Run: ../venv/bin/python scenario_order_with_approval.py
"""

from _common import run_agent

run_agent("I want to order 1 mechanical keyboard. Search for it, check stock, then order it.")
