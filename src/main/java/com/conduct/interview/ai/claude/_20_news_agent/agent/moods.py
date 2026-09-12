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
        "is positive. This applies ONLY to sites INSIDE RUSSIA that belong to Russia — "
        "a fire, explosion, or accident at a factory/warehouse in a THIRD COUNTRY "
        "(e.g. a NATO/allied country's ammunition or weapons producer, even one that "
        "supplies Ukraine) is NOT this rule and must be evaluated as bad/neutral news "
        "for Ukraine instead (it can only hurt Ukraine's weapons supply, never help it) — "
        "see ABSOLUTE DROPS below. CRITICAL: direction and ownership both matter — "
        "Russia attacking a Ukrainian city, or an accident damaging an ALLY's or "
        "Ukraine's own weapons/ammo supply, is the OPPOSITE of this rule and must "
        "always be DROPPED; "
        "(4) global oil price DECREASING (e.g. Brent crude falling/dropping); "
        "(5) the USA winning or succeeding against Iran (military, diplomatic, "
        "or sanctions success). "
        "ABSOLUTE DROPS — these must NEVER appear in positive, no exceptions: "
        "ANY item where Russia/enemy (РФ/росіяни/ворог/окупанти/загарбники) is "
        "the one launching missiles, drones (БпЛА/безпілотники), or other attacks "
        "ON UKRAINIAN TERRITORY (Київщина, Житомирщина, Харківщина, Одещина, "
        "Дніпропетровщина, Запоріжжя, Херсон, Краматорськ, Миколаїв, Суми, "
        "Полтава, Черкаси, Вінниця, Львів, etc.) — DROP IT even if Ukraine's "
        "air defense (ППО) shot down many drones/missiles (e.g. 'збито 110 цілей' "
        "— that is a defensive result during a Russian attack, NOT a positive event); "
        "Ukrainian civilian or military casualties, deaths, or injuries, INCLUDING "
        "rescuers/emergency responders/firefighters (рятувальники/вогнеборці/пожежники) "
        "hurt while responding to a Russian attack — an item like 'Russians struck "
        "rescuers, firefighter wounded' is a Ukrainian casualty, not a Russian one, "
        "and must be DROPPED; "
        "Russian forces advancing, capturing, or occupying Ukrainian territory; "
        "any fire, explosion, or accident (NOT a confirmed Ukrainian/allied strike) at "
        "a weapons/ammunition producer or storage site OUTSIDE Russia, including in "
        "countries that supply arms to Ukraine (e.g. a Bulgarian, Czech, or other NATO "
        "country's ammunition factory catching fire) — this reduces supply available "
        "to Ukraine's side and is bad/neutral news, never positive, even though it "
        "superficially resembles rule (3)'s 'explosion at a depot' pattern. "
        "DROP everything else not in (1)-(5): diplomacy, aid announcements, "
        "Ukraine's financial needs or funding gaps (e.g. 'Ukraine needs $X billion', "
        "budget shortfalls, reconstruction cost estimates, energy sector investment "
        "requirements — these are about Ukraine lacking money, not about Ukraine winning); "
        "culture/sport, crime, corruption, oil prices rising, Iran attacking anyone, "
        "Russia growing oil/gas revenue or evading sanctions."
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
        "military or civilian casualties and deaths, INCLUDING rescuers/emergency "
        "responders/firefighters hurt while responding to a Russian attack; confirmed "
        "Russian territorial advances or gains (e.g. 'Russians advanced near X'); "
        "Russian strikes on Ukrainian cities causing deaths or serious damage; "
        "Ukrainian economic collapse or major corruption scandals; any fire, "
        "explosion, or accident at a weapons/ammunition producer or storage site in a "
        "country that supplies arms to Ukraine (this hurts Ukraine's supply, so it is "
        "harmful, not neutral)."
    ),
}

# CATEGORY_RULES — a second, independent tag applied on top of MOOD_RULES,
# used only to highlight/label items in the UI and Telegram messages (see
# classify.py's get_categories). Kept separate from MOOD_RULES because it
# answers a different question: not "keep or drop" but "which of these
# three headline-grabbing story types is this, if any". "other" covers
# everything else that still passed the "positive" mood filter (e.g. oil
# price drops, USA vs Iran) — nothing to highlight, but still shown.
CATEGORY_RULES = (
    "For a Ukrainian reader following the war. Classify EACH numbered item into "
    "EXACTLY ONE of these categories:\n"
    "\"strike\" — a Ukrainian/allied drone (БпЛА/безпілотник) or missile strike "
    "hitting a target INSIDE RUSSIA: a refinery (НПЗ), factory, chemical plant "
    "(хімзавод), depot, airfield, or other military/industrial site on Russian "
    "territory. Only strikes ON Russian soil count — never a Russian strike on "
    "Ukraine, and never an accident/fire at a facility outside Russia (e.g. in an "
    "allied country).\n"
    "\"losses\" — confirmed Russian military losses: personnel killed/wounded, "
    "named equipment or vehicles destroyed, or a running war-losses tally.\n"
    "\"economy\" — Russian economic problems: sanctions impact, fuel/refinery "
    "shortages, budget or currency trouble, falling oil revenue, oil price drops "
    "that hurt Russia.\n"
    "\"other\" — anything that doesn't fit the three categories above.\n"
    "Respond with ONLY a JSON array of category strings, one per item, in the same "
    "order as the numbered list, e.g. [\"strike\",\"other\",\"losses\"]. No prose, "
    "no explanation, no markdown fences."
)

CATEGORY_LABELS = {
    "strike": "🔥 Удар по РФ",
    "losses": "💀 Втрати ворога",
    "economy": "📉 Економіка РФ",
}
