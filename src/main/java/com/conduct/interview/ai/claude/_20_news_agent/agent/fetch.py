"""Завантаження RSS-стрічки → список dict {title, link, description, pubDate}."""

import logging
import xml.etree.ElementTree as ET

import requests

from .config import FETCH_HEADERS, RSS_URL

log = logging.getLogger("news_agent.fetch")


def fetch_items(limit: int = 80) -> list[dict]:
    """Завантажує до `limit` новин з RSS."""
    log.info("Fetching RSS (limit=%d): %s", limit, RSS_URL)
    resp = requests.get(RSS_URL, headers=FETCH_HEADERS, timeout=10)
    resp.raise_for_status()
    root = ET.fromstring(resp.content)

    items = []
    for item in root.findall("./channel/item")[:limit]:
        items.append({
            "title":       (item.findtext("title")       or "").strip(),
            "link":        (item.findtext("link")        or "").strip(),
            "description": (item.findtext("description") or "").strip(),
            "pubDate":     (item.findtext("pubDate")     or "").strip(),
        })

    log.info("Fetched %d items", len(items))
    return items
