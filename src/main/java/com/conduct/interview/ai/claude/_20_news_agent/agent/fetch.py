"""
FETCH — the agent's "perception" step.

An agent can only reason about what you hand it. This module's only job is
turning an external, messy format (RSS/XML, or raw HTML) into a flat list
of plain Python dicts — nothing here calls an LLM. Keeping perception
separate from reasoning (providers.py / classify.py) means the rest of the
agent never notices which source is active.

Two sources, switchable via config.FETCH_SOURCE (env var FETCH_SOURCE, or
DEFAULT_FETCH_SOURCE in config.py for local dev — same pattern as the LLM
provider switch):

  "rss"   parses the site's own RSS feed. Richer (title + description +
          reliable pubDate), ~80 items.
  "html"  scrapes the homepage's article teasers directly. Title + link
          only (no description, patchy pubDate), ~30 items. See
          config.py's comment for the measured "html isn't actually
          faster" trade-off.
"""

import logging
import re
import xml.etree.ElementTree as ET

import requests
from bs4 import BeautifulSoup

from .config import FETCH_HEADERS, FETCH_SOURCE, HTML_URL, RSS_URL

log = logging.getLogger("news_agent.fetch")

# Matches an article URL on censor.net regardless of section
# (news/videonews/photonews) or the optional "/ua/" locale prefix.
_ARTICLE_LINK_RE = re.compile(r"https://censor\.net/(ua/)?(news|videonews|photonews)/\d+/")


def _fetch_items_rss(limit: int) -> list[dict]:
    log.info("Fetching RSS feed (limit=%d): %s", limit, RSS_URL)
    resp = requests.get(RSS_URL, headers=FETCH_HEADERS, timeout=10)
    resp.raise_for_status()
    root = ET.fromstring(resp.content)

    items = []
    for item in root.findall("./channel/item")[:limit]:
        items.append({
            "title": (item.findtext("title") or "").strip(),
            "link": (item.findtext("link") or "").strip(),
            "description": (item.findtext("description") or "").strip(),
            "pubDate": (item.findtext("pubDate") or "").strip(),
        })
    return items


def _fetch_items_html(limit: int) -> list[dict]:
    log.info("Fetching HTML homepage (limit=%d): %s", limit, HTML_URL)
    resp = requests.get(HTML_URL, headers=FETCH_HEADERS, timeout=10)
    resp.raise_for_status()
    soup = BeautifulSoup(resp.content, "html.parser")

    items = []
    seen_links = set()
    for a in soup.find_all("a", href=_ARTICLE_LINK_RE):
        href = a["href"]
        title = a.get("title")
        if not title or href in seen_links:
            continue
        seen_links.add(href)

        # The publish time isn't in the <a> itself — walk up a few parent
        # containers to find the <time> tag the page renders alongside it.
        # Not every card layout on the homepage nests the same way, so this
        # sometimes comes up empty; that's fine, pubDate is cosmetic here.
        pub_date = ""
        node = a
        for _ in range(5):
            node = node.parent
            if node is None:
                break
            time_tag = node.find("time")
            if time_tag and time_tag.get("datetime"):
                pub_date = time_tag["datetime"]
                break

        items.append({
            "title": title.strip(),
            "link": href,
            "description": "",  # homepage teasers don't include a summary
            "pubDate": pub_date,
        })
        if len(items) >= limit:
            break

    return items


def fetch_items(limit: int = 80) -> list[dict]:
    """Download and parse the active source into a flat list of dicts:
    {"title", "link", "description", "pubDate"}. Which source is "active"
    is entirely config.FETCH_SOURCE's call — see this module's docstring."""
    if FETCH_SOURCE == "html":
        items = _fetch_items_html(limit)
    else:
        items = _fetch_items_rss(limit)

    log.info("Fetched %d items (source=%s)", len(items), FETCH_SOURCE)
    return items
