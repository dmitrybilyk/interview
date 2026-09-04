"""
Shared client, pricing table, and cost helper for _8_token_counting.
Not runnable on its own — imported by the demo scripts in this directory.
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Approximate pricing (per 1M tokens, Aug 2025)
PRICES = {
    "claude-haiku-4-5-20251001": {"in": 0.80,  "out": 4.00},
    "claude-sonnet-4-6":         {"in": 3.00,  "out": 15.00},
    "claude-opus-4-7":           {"in": 15.00, "out": 75.00},
}

def estimate_cost(model: str, input_tokens: int, output_tokens: int = 0) -> str:
    p = PRICES[model]
    cost = (input_tokens / 1_000_000 * p["in"]) + (output_tokens / 1_000_000 * p["out"])
    return f"${cost:.6f}"
