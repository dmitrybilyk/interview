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
        "(3) strikes/attacks ON RUSSIAN TERRITORY — drone (БпЛА/безпілотник) or "
        "missile strikes hitting Russian cities, refineries (НПЗ), chemical plants "
        "(хімзаводи), depots, airfields, military or industrial sites INSIDE RUSSIA "
        "(e.g. Tolyatti/Тольятті, Samara/Самара, Saratov, Engels, Bryansk, Belgorod, "
        "Kursk, Voronezh, Lipetsk, Rostov, Krasnodar, Novorossiysk, etc.). "
        "KEEP these even if the article does not explicitly say 'Ukrainian drones' — "
        "any attack causing fire/explosion at a Russian industrial or military site "
        "is positive. CRITICAL: direction matters — Russia attacking a Ukrainian city "
        "is the OPPOSITE and must always be DROPPED; "
        "(4) global oil price DECREASING (e.g. Brent crude falling/dropping); "
        "(5) the USA winning or succeeding against Iran (military, diplomatic, "
        "or sanctions success). "
        "DROP everything else: Ukrainian casualties/damage, diplomacy, aid "
        "announcements, culture/sport, crime, corruption, oil prices rising, "
        "Iran attacking/bombing anyone, Russia growing its oil/gas revenue or "
        "successfully evading sanctions (e.g. 'shadow fleet' tankers, record "
        "exports/production), and anything not matching (1)-(5) above."
    ),
    "mostly_positive": (
        "This is for a Ukrainian reader. Keep items that are either clearly "
        "positive for Ukraine OR neutral/contextual — but drop anything that is "
        "directly and clearly bad for Ukraine. "
        "KEEP: everything from the positive mood (Russian military losses, strikes "
        "on Russian territory, Russian economic problems, oil price falling, USA "
        "winning against Iran); "
        "ALSO KEEP: diplomatic and geopolitical developments even if uncomfortable "
        "(e.g. Russia being invited to international forums like G20, UN meetings, "
        "or energy summits — these reflect global context, not a Ukrainian defeat); "
        "international support and aid for Ukraine; general war situation analysis "
        "without confirmed Russian gains; reconstruction news; cultural/sport news. "
        "DROP ONLY items that are directly and clearly harmful: confirmed Ukrainian "
        "military or civilian casualties and deaths; confirmed Russian territorial "
        "advances or gains (e.g. 'Russians advanced near X'); Russian strikes on "
        "Ukrainian cities causing deaths or serious damage; Ukrainian economic "
        "collapse or major corruption scandals."
    ),
}
