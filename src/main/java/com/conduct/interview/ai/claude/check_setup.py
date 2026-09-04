#!/usr/bin/env python3
"""
check_setup.py — sanity check for this Claude API practice repo.

Run this whenever you're not sure the environment still works, before
starting a lesson, or before an interview/exam where you don't want to
find out the key expired mid-demo.

Usage:
    python3 check_setup.py          # static checks only, free, no API call
    python3 check_setup.py --live   # + one real API call to confirm the key works
"""

import os
import sys


def check(label: str, ok: bool, detail: str = "") -> bool:
    status = "OK  " if ok else "FAIL"
    suffix = f" — {detail}" if detail else ""
    print(f"[{status}] {label}{suffix}")
    return ok


def main() -> int:
    live = "--live" in sys.argv
    all_ok = True

    all_ok &= check(
        "Python >= 3.8",
        sys.version_info >= (3, 8),
        f"found {sys.version.split()[0]}",
    )

    anthropic = None
    try:
        import anthropic  # noqa: F401
        version = getattr(anthropic, "__version__", "unknown")
        all_ok &= check("anthropic package installed", True, f"v{version}")
    except ImportError:
        all_ok &= check(
            "anthropic package installed", False, "run: pip install anthropic"
        )

    api_key = os.environ.get("ANTHROPIC_API_KEY")
    all_ok &= check(
        "ANTHROPIC_API_KEY set",
        bool(api_key),
        "" if api_key else "not set — source setup.sh, or export it yourself",
    )

    if not live:
        print("\nStatic checks only. Run with --live to confirm the key actually works.")
        return 0 if all_ok else 1

    if not (anthropic and api_key):
        print("\nSkipping live call — fix the checks above first.")
        return 1

    try:
        client = anthropic.Anthropic(api_key=api_key)
        response = client.messages.create(
            model="claude-haiku-4-5-20251001",
            max_tokens=10,
            messages=[{"role": "user", "content": "Reply with exactly one word: pong"}],
        )
        text = "".join(
            block.text for block in response.content if block.type == "text"
        ).strip()
        all_ok &= check("Live API call", "pong" in text.lower(), f"got: {text!r}")
    except Exception as exc:  # noqa: BLE001 — want any failure surfaced here
        all_ok &= check("Live API call", False, str(exc))

    return 0 if all_ok else 1


if __name__ == "__main__":
    sys.exit(main())
