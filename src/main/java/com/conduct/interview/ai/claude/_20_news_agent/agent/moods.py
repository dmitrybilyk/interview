"""
MOODS — the agent's "policy": what counts as a match, written in plain
language for the LLM to apply.

This is the ONLY file you'd touch to change what the agent looks for. It
doesn't know or care which provider answers it (providers.py) or how the
answer gets cached (classify.py) — it's pure task definition, which makes
it the easiest place to experiment: edit a rule, rerun cli.py, see the
list change.
"""

MOOD_RULES = {
    "positive": (
        "This is for a Ukrainian reader, so 'positive' means bad news FOR RUSSIA "
        "(and its allies) or good news for Ukraine's side, not the absence of "
        "war news. Keep ONLY items about: "
        "(1) Russian military losses — items that explicitly describe actual "
        "confirmed casualties or destruction: bodies/corpses of occupiers "
        "found, a specific number of soldiers killed/eliminated/wounded, "
        "named equipment or vehicles destroyed, or a running tally of total "
        "war losses (e.g. 'total Russian losses: N personnel'). KEEP these "
        "even if the wording also mentions an attack or infiltration attempt "
        "— what matters is whether THIS item states Russians died/were hurt/ "
        "lost equipment, not whether an attack happened. "
        "Do NOT keep vague front-line activity with no loss figures: 'X "
        "attacks repelled', 'N battles/clashes today', 'Ukraine defended "
        "against N assaults' — only keep these if they also state a specific "
        "casualty or equipment-destroyed number. "
        "Do NOT keep counter-intelligence/law-enforcement catches (SBU "
        "exposing a spy, collaborator, or artillery/strike spotter/coordinator) "
        "unless the item itself states this directly caused confirmed Russian "
        "military casualties — catching a collaborator alone is not a Russian "
        "loss; "
        "(2) Russian economic problems — sanctions impact, fuel/refinery "
        "shortages, budget or currency trouble, falling oil revenue; "
        "(3) strikes/attacks ON RUSSIAN TERRITORY — drone or missile strikes "
        "on Russian refineries (НПЗ), depots, airfields, military or industrial "
        "sites inside Russia; "
        "(4) global oil price DECREASING (e.g. Brent crude falling/dropping); "
        "(5) the USA winning or succeeding against Iran (military, diplomatic, "
        "or sanctions success). "
        "DROP everything else: Ukrainian casualties/damage, diplomacy, aid "
        "announcements, culture/sport, crime, corruption, oil prices rising, "
        "Iran attacking/bombing anyone, Russia growing its oil/gas revenue or "
        "successfully evading sanctions (e.g. 'shadow fleet' tankers, record "
        "exports/production), and anything not matching (1)-(5) above."
    ),
    "negative": (
        "Keep items that report the hard, distressing side of the news FOR "
        "UKRAINE or its allies: Ukrainian deaths, injuries, casualties, attack "
        "damage, economic hardship, crime, corruption, threats, war losses. "
        "ALSO keep: global oil price INCREASING (e.g. Brent crude rising/jumping) "
        "— higher oil prices mean more money for Russia, which is bad for Ukraine; "
        "Russia GROWING its oil/gas revenue, exports or production, or "
        "successfully evading sanctions (e.g. expanding its 'shadow fleet' of "
        "tankers, record output despite sanctions) — this is Russia winning "
        "economically, the opposite of the 'positive' mood's Russian economic "
        "PROBLEMS; and Iran bombing/attacking anyone (Iran as the aggressor). "
        "DROP purely uplifting items and anything about Russian losses/problems "
        "(aid announcements, diplomatic pleasantries, sport/culture, rescues "
        "with a happy ending, Russian casualties, strikes on Russian soil, "
        "oil prices falling, the USA winning against Iran)."
    ),
}
