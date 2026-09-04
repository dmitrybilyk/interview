"""
Shared helpers for _13_packaging_for_reuse — not runnable on its own.

load_config()  — documented defaults live in config.default.json; a customer
                 override file layers on top. The installing team edits a
                 JSON file, never this code or the agent loop itself.
audit_log()    — the audit trail every accelerator ships with: what
                 happened, under what identity, on what data. Appends one
                 JSON line per event to audit.log next to this file.
"""
import datetime
import json
import os

HERE = os.path.dirname(os.path.abspath(__file__))
DEFAULT_CONFIG_PATH = os.path.join(HERE, "config.default.json")
AUDIT_LOG_PATH = os.path.join(HERE, "audit.log")


def load_config(override_path: str = None) -> dict:
    with open(DEFAULT_CONFIG_PATH) as f:
        config = json.load(f)
    if override_path and os.path.exists(override_path):
        with open(override_path) as f:
            config.update(json.load(f))
    return config


def audit_log(event: str, identity: str, data: dict) -> None:
    entry = {
        "ts": datetime.datetime.utcnow().isoformat() + "Z",
        "event": event,
        "identity": identity,
        "data": data,
    }
    with open(AUDIT_LOG_PATH, "a") as f:
        f.write(json.dumps(entry) + "\n")
