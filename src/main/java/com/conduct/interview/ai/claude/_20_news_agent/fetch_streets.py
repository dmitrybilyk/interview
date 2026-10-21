#!/usr/bin/env python3
"""
Fetch street names from OpenStreetMap and append them to zones.py for both bots.
Run: python fetch_streets.py
"""

import json
import math
import re
import time
import requests
from pathlib import Path

HERE = Path(__file__).parent

# ── Zone centers (lat, lon) ───────────────────────────────────────────────────

LVIV_ZONE_CENTERS = {
    "center": (49.8397, 24.0297),
    "north":  (49.900,  24.020),
    "east":   (49.845,  24.130),
    "west":   (49.840,  23.940),
    "south":  (49.770,  24.030),
}

KYIV_ZONE_CENTERS = {
    "center": (50.4501, 30.5234),
    "north":  (50.530,  30.500),
    "east":   (50.460,  30.660),
    "west":   (50.450,  30.360),
    "south":  (50.360,  30.530),
}

# ── Generic names to skip ─────────────────────────────────────────────────────

SKIP_WORDS = {
    "нова", "нове", "новий", "стара", "старе", "старий", "нові", "старі",
    "велика", "велике", "великий", "мала", "мале", "малий",
    "перша", "перше", "перший", "друга", "друге", "другий",
    "третя", "четверта", "п'ята",
    "центральна", "центральне", "центральний",
    "паркова", "садова", "садовий", "лісова", "лісовий",
    "польова", "річна", "гірська", "степова",
    "тиха", "тихий", "широка", "широкий",
    "коротка", "довга", "пряма",
    "перемоги", "незалежності", "свободи", "миру",
}

# ── Overpass ──────────────────────────────────────────────────────────────────

OVERPASS_ENDPOINTS = [
    "https://overpass-api.de/api/interpreter",
    "https://lz4.overpass-api.de/api/interpreter",
    "https://z.overpass-api.de/api/interpreter",
]


def overpass(query: str, timeout: int = 90) -> dict:
    for url in OVERPASS_ENDPOINTS:
        try:
            r = requests.post(url, data={"data": query}, timeout=timeout)
            if r.status_code == 200:
                return r.json()
            print(f"    {url}: HTTP {r.status_code}")
        except Exception as e:
            print(f"    {url}: {e}")
        time.sleep(3)
    return {"elements": []}


def fetch_streets(lat: float, lon: float, radius_km: int) -> list[dict]:
    q = f"""
[out:json][timeout:90];
(
  way["highway"~"^(primary|secondary|tertiary|residential|living_street|pedestrian|unclassified)$"]
     ["name"]
     (around:{radius_km * 1000},{lat},{lon});
);
out center tags;
"""
    print(f"  Querying OSM (radius={radius_km}km)...")
    data = overpass(q)
    seen, streets = set(), []
    for el in data.get("elements", []):
        name = el.get("tags", {}).get("name", "").strip()
        if not name or name in seen:
            continue
        center = el.get("center", {})
        if not center:
            continue
        seen.add(name)
        streets.append({"name": name, "lat": center["lat"], "lon": center["lon"]})
    print(f"  → {len(streets)} unique street names")
    return streets

# ── Stem extraction ───────────────────────────────────────────────────────────

def make_stem(name: str) -> str | None:
    n = name.lower().strip()

    if len(n) < 4 or n in SKIP_WORDS or n.isdigit():
        return None

    # Adjective hard suffix → trim to consonant cluster
    # личаківська → личаківськ,  героїчна → героїчн,  зелена → зелен
    adj_rules = [
        ("ська", 3), ("зька", 3), ("цька", 3),
        ("ський", 4), ("зький", 4), ("цький", 4),
        ("ської", 4), ("ській", 4), ("ського", 4), ("ськім", 4),
    ]
    for suffix, trim in adj_rules:
        if n.endswith(suffix) and len(n) - trim >= 4:
            return n[:-trim]

    # -на/-ня adjectives: зелена → зелен
    if n.endswith("на") and len(n) > 5:
        return n[:-1]

    # Personal genitive names: Шевченка → Шевченк
    if n.endswith("а") and len(n) > 5:
        root = n[:-1]
        if root not in SKIP_WORDS:
            return root

    return n

# ── Zone assignment ───────────────────────────────────────────────────────────

def haversine(lat1, lon1, lat2, lon2) -> float:
    R = 6371
    φ1, φ2 = math.radians(lat1), math.radians(lat2)
    a = (math.sin(math.radians(lat2 - lat1) / 2) ** 2
         + math.cos(φ1) * math.cos(φ2)
         * math.sin(math.radians(lon2 - lon1) / 2) ** 2)
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))


def assign_zone(lat, lon, zone_centers: dict, max_km: float = 18.0) -> str | None:
    closest = min(zone_centers, key=lambda k: haversine(lat, lon, *zone_centers[k]))
    if haversine(lat, lon, *zone_centers[closest]) > max_km:
        return None
    return closest

# ── Build per-zone stem lists ─────────────────────────────────────────────────

def build_stem_sets(streets: list[dict], zone_centers: dict) -> dict:
    result: dict[str, set] = {k: set() for k in ["all", *zone_centers]}
    for s in streets:
        stem = make_stem(s["name"])
        if not stem:
            continue
        zone = assign_zone(s["lat"], s["lon"], zone_centers)
        if not zone:
            continue
        result[zone].add(stem)
        result["all"].add(stem)
    return {k: sorted(v) for k, v in result.items()}

# ── Patch zones.py in-place ───────────────────────────────────────────────────

def patch_zones_file(path: Path, new_stems: dict) -> None:
    """Append street stems to each zone's stem list, avoiding duplicates."""
    src = path.read_text()

    # Parse existing stems per zone from the file
    existing: dict[str, set] = {}
    for zone in new_stems:
        # Find the zone block and its current stems list
        # We'll use a simple approach: read the zones module
        pass

    # Easier: just append a generated block at the end
    lines = ["\n\n# ── Street stems (auto-generated by fetch_streets.py) ──────────────\n",
             "STREET_STEMS = {\n"]
    for zone, stems in new_stems.items():
        lines.append(f'    "{zone}": [\n')
        for stem in stems:
            lines.append(f'        "{stem}",\n')
        lines.append("    ],\n")
    lines.append("}\n\n")
    lines.append("# Merge into ZONES\n")
    lines.append("for _zone, _stems in STREET_STEMS.items():\n")
    lines.append("    if _zone in ZONES:\n")
    lines.append("        _existing = set(ZONES[_zone]['stems'])\n")
    lines.append("        ZONES[_zone]['stems'] += [s for s in _stems if s not in _existing]\n")

    # Remove old auto-generated block if present
    marker = "\n\n# ── Street stems (auto-generated"
    if marker in src:
        src = src[:src.index(marker)]

    path.write_text(src + "".join(lines))
    print(f"  Updated {path}")

# ── Main ──────────────────────────────────────────────────────────────────────

def main():
    print("=== Lviv ===")
    lviv_streets = fetch_streets(49.8397, 24.0297, 20)
    if lviv_streets:
        lviv_stems = build_stem_sets(lviv_streets, LVIV_ZONE_CENTERS)
        for zone, stems in lviv_stems.items():
            print(f"  {zone:8s}: {len(stems)} stems")
        patch_zones_file(HERE / "shahed_bot" / "zones.py", lviv_stems)
        Path(HERE / "lviv_street_stems.json").write_text(
            json.dumps(lviv_stems, ensure_ascii=False, indent=2))
    else:
        print("  No data — Overpass unavailable")

    print("\n=== Kyiv ===")
    kyiv_streets = fetch_streets(50.4501, 30.5234, 25)
    if kyiv_streets:
        kyiv_stems = build_stem_sets(kyiv_streets, KYIV_ZONE_CENTERS)
        for zone, stems in kyiv_stems.items():
            print(f"  {zone:8s}: {len(stems)} stems")
        patch_zones_file(HERE / "kyiv_bot" / "zones.py", kyiv_stems)
        Path(HERE / "kyiv_street_stems.json").write_text(
            json.dumps(kyiv_stems, ensure_ascii=False, indent=2))
    else:
        print("  No data — Overpass unavailable")


if __name__ == "__main__":
    main()
