"""
Topic: packaging an eval — the test questions and the grading rules,
       kept together, used as a pass/fail check before you ship
Cert notes section: "Packaging for reuse"
Run:
    export ANTHROPIC_API_KEY=$(cat ../key.txt)
    python3 _3_eval_gate.py

The test questions live in eval_dataset.json, the score you need to keep
hitting lives in eval_baseline.json. A new team can run this exact file in
their own setup to check the agent still works there. It's also what you'd
run before switching to a new model version — same questions, same
grading, compare the new score to the old one before you flip the switch.
"""
import copy
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from _1_parameterize_agent_template import run_agent
from _pkg import audit_log, load_config

HERE = os.path.dirname(os.path.abspath(__file__))


def load_json(name: str):
    with open(os.path.join(HERE, name)) as f:
        return json.load(f)


def is_subsequence(expected: list, trace: list) -> bool:
    """True if every tool in `expected` appears in `trace`, in that order
    (other calls in between are fine)."""
    it = iter(trace)
    return all(tool in it for tool in expected)


def run_case(case: dict, base_config: dict):
    config = copy.deepcopy(base_config)
    config["require_approval"] = False  # eval runs unattended; gate is checked structurally below
    config["_trace"] = []
    config["identity"] = f"eval:{case['id']}"

    run_agent(case["request"], config)
    trace = config["_trace"]

    calls_ok = is_subsequence(case["expected_tools_called"], trace)

    gated_tools = case.get("requires_approval_for", [])
    gate_ok = all(
        tool in base_config["destructive_tools"] and base_config["require_approval"]
        for tool in gated_tools
    )

    passed = calls_ok and gate_ok
    audit_log(
        "eval_case",
        config["identity"],
        {"case": case["id"], "trace": trace, "calls_ok": calls_ok, "gate_ok": gate_ok, "passed": passed},
    )
    return passed, trace


def main() -> int:
    dataset = load_json("eval_dataset.json")
    baseline = load_json("eval_baseline.json")
    base_config = load_config()  # defaults only — this gates the shipped config, not a customer override

    results = []
    for case in dataset:
        passed, trace = run_case(case, base_config)
        print(f"[{'PASS' if passed else 'FAIL'}] {case['id']} — trace: {trace}")
        results.append(passed)

    score = sum(results) / len(results) if results else 0.0
    print(f"\nScore: {score:.2f}  (baseline: {baseline['baseline_score']:.2f})")

    if score < baseline["baseline_score"]:
        print("GATE FAILED — do not promote/deploy.")
        return 1
    print("Gate passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
