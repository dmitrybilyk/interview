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
        "You are filtering news for a Ukrainian reader. Check each item in TWO STEPS.\n\n"
        "STEP 1 — REJECT first. If ANY of the following is true, the item is REJECTED "
        "(do not keep it, no matter what else it says):\n"
        "R1. Enemy/Russia (ворог/РФ/росіяни/окупанти/загарбники) attacked, struck, "
        "hit, shelled, bombed, terrorized, or damaged ANYTHING in Ukraine — cities, "
        "villages, shops (магазин), gas stations (АЗС), factories or enterprises "
        "(завод/підприємство/Запоріжсталь/Азовсталь/etc.), infrastructure, houses, "
        "hospitals — by any weapon (drone/БпЛА, missile/ракета, artillery, glide bomb). "
        "Signals: 'ворог ударив', 'ворог атакував', 'РФ атакувала', 'росіяни ударили', "
        "'обстріляли', 'атакували', 'влучили по', 'тероризують' + Ukrainian location → REJECT.\n"
        "R2. Any Ukrainian people killed, died, injured, or wounded — civilians, "
        "soldiers, workers, rescuers, firefighters. Signals: 'загиблих', 'поранених', "
        "'постраждалих', 'є жертви', 'загинули' in a Ukrainian context → REJECT.\n"
        "R3. Ukraine's air defense shot down Russian drones/missiles — this means "
        "Russia was attacking Ukraine ('збито 110 цілей' = Russian attack on Ukraine) → REJECT.\n"
        "R4. Russian/enemy forces advanced, captured, or occupied Ukrainian territory → REJECT.\n"
        "R5. Ukraine lacks money, needs funding, has budget shortfalls, reconstruction "
        "costs, energy investment needs ('Україні потрібно $X млрд', etc.) → REJECT.\n"
        "R6. Russia getting stronger: nuclear warheads growing, army size increasing, "
        "new weapons deployed, record defence spending → REJECT.\n"
        "R7. Russia growing oil/gas revenue, successfully evading sanctions, shadow "
        "fleet expanding, record exports → REJECT.\n"
        "R8. Fire/explosion/accident at a weapons or ammo factory in an allied country "
        "that supplies Ukraine (hurts Ukraine's supply) → REJECT.\n\n"
        "STEP 2 — KEEP only if the item was NOT rejected in Step 1 AND clearly matches "
        "one of these:\n"
        "K1. Russian military losses: specific numbers of Russian soldiers "
        "killed/wounded/eliminated, named Russian equipment destroyed, or a total "
        "Russian war-losses tally. NOT vague front-line activity without casualty numbers.\n"
        "K2. Russian economic problems: sanctions impact, refinery/fuel shortages, "
        "currency or budget trouble, falling oil revenue.\n"
        "K3. Strike ON RUSSIAN TERRITORY: drones or missiles hitting Russian cities, "
        "refineries (НПЗ), chemical plants (хімзаводи), depots, airfields, or military/"
        "industrial sites INSIDE RUSSIA (Тольятті, Самара, Белгород, Курськ, Воронеж, "
        "Липецьк, Ростов, Краснодар, Новоросійськ, Саратов, Енгельс, etc.). "
        "Keep even without 'Ukrainian drones' stated explicitly — fire/explosion at "
        "a Russian site is enough. ONLY Russian-territory sites qualify.\n"
        "K4. Global oil price FALLING/DECREASING.\n"
        "K5. USA winning or succeeding against Iran.\n\n"
        "If in doubt → REJECT. Default answer is to drop, not keep."
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
    "\"losses\" — confirmed RUSSIAN military losses ONLY: Russian personnel "
    "killed/wounded/eliminated, Russian named equipment or vehicles destroyed, "
    "or a running Russian war-losses tally. NEVER use \"losses\" for Ukrainian "
    "casualties, injured Ukrainian workers/civilians, or damage to Ukrainian "
    "property — those are \"other\" (and should not have passed the positive "
    "filter at all).\n"
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
