"""
Run the agent from the terminal — no Flask, no browser, no Telegram.
The fastest way to test a change to moods.py or try the other provider.

Usage:
    venv/bin/python cli.py positive
    venv/bin/python cli.py negative
    venv/bin/python cli.py positive --verbose     # see every fetch/cache/LLM step

Switch provider for one run without touching config.py:
    LLM_PROVIDER=groq venv/bin/python cli.py positive

Or permanently for all local runs: edit DEFAULT_PROVIDER in agent/config.py.
"""

import argparse
import logging

from agent import MOOD_RULES, get_filtered_news


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mood", choices=sorted(MOOD_RULES) + ["all"])
    parser.add_argument(
        "--verbose", "-v", action="store_true",
        help="show INFO-level logs: fetch/cache/LLM steps as they happen",
    )
    args = parser.parse_args()

    logging.basicConfig(
        level=logging.INFO if args.verbose else logging.WARNING,
        format="%(asctime)s %(levelname)-7s [%(name)s] %(message)s",
        datefmt="%H:%M:%S",
    )

    items = get_filtered_news(args.mood)

    print(f"\n{len(items)} item(s) matched mood={args.mood!r}:\n")
    for item in items:
        print(f"- {item['title']}")
        print(f"  {item['link']}")
    print()


if __name__ == "__main__":
    main()
