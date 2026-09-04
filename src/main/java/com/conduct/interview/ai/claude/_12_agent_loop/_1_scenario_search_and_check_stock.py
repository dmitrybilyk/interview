"""
Topic: Agent loop — read-only scenario (search + check stock, no approval needed)
Cert notes section: "Building Production Agents"
Run: ../venv/bin/python _1_scenario_search_and_check_stock.py
"""

from _common import run_agent

run_agent("Find me a laptop and check if it's in stock.")
