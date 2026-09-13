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
        "You are filtering news for a Ukrainian reader. This filter is EXTREMELY STRICT.\n"
        "Keep ONLY items that clearly match ONE of these three types. Reject everything else.\n\n"
        "K1. MORNING LOSSES REPORT — the official daily General Staff / МО Ukraine briefing "
        "that states the total number of Russian soldiers killed or eliminated in the past "
        "24 hours. Typical signals: 'Генштаб', 'ЗСУ ліквідували X ворогів', 'втрати ворога "
        "за добу', 'загальні втрати', 'знищено X окупантів', specific death-toll number + "
        "'за минулу добу' or 'з початку доби'. "
        "DO NOT keep: general front-line battle reports, equipment-only counts, any item "
        "without a specific daily Russian death toll figure.\n"
        "K2. RUSSIAN ECONOMY GETTING WORSE — news about deterioration of the Russian economy: "
        "ruble falling/weakening, budget deficit growing, inflation rising, companies leaving "
        "Russia, sanctions biting harder, oil/gas revenue dropping, industrial output "
        "shrinking, banking problems, consumer prices rising inside Russia. "
        "DO NOT keep: Russian economy surviving, evading sanctions, or growing.\n"
        "K3. UKRAINIAN STRIKE ON RUSSIAN TERRITORY — drones (БпЛА/безпілотники) or missiles "
        "launched BY UKRAINE hitting any target INSIDE RUSSIA: oil refineries (НПЗ), fuel "
        "depots, oil storage, warehouses (склади), military bases, airfields, factories, "
        "ammunition depots, infrastructure — on Russian soil (Белгород, Курськ, Брянськ, "
        "Воронеж, Ростов, Краснодар, Самара, Тольятті, Саратов, Енгельс, Липецьк, "
        "Новоросійськ, Москва, etc.). Fire or explosion at a Russian-territory military or "
        "industrial site is enough — explicit mention of Ukrainian drones not required. "
        "DO NOT keep: strikes on Ukraine, strikes inside occupied Ukrainian territory, "
        "air-defense shootdowns over Ukraine.\n\n"
        "If the item does not clearly fit K1, K2, or K3 → REJECT. When in doubt → REJECT."
    ),
    "mostly_positive": (
        "You are filtering news for a Ukrainian reader. Check each item in TWO STEPS.\n\n"
        "STEP 1 — REJECT first. If ANY of the following is true, the item is REJECTED "
        "(do not keep it, no matter what else it says):\n"
        "R1. Enemy/Russia (ворог/РФ/росіяни/окупанти/загарбники) attacked, struck, "
        "hit, shelled, bombed, terrorized, or damaged ANYTHING in Ukraine — cities, "
        "villages, shops (магазин), gas stations (АЗС), factories or enterprises "
        "(завод/підприємство/Запоріжсталь/Азовсталь/etc.), infrastructure, houses, "
        "hospitals — by any weapon (drone/БпЛА, missile/ракета, artillery, glide bomb). "
        "Signals: 'ворог ударив', 'ворог атакував', 'ворожа атака', 'РФ атакувала', "
        "'росіяни ударили', 'обстріляли', 'атакували', 'влучили по', 'тероризують', "
        "'залишилися без світла', 'без електрики', 'знеструмлено', 'пошкоджено' "
        "+ Ukrainian location → REJECT.\n"
        "R2. Any Ukrainian people killed, died, injured, or wounded — civilians, "
        "soldiers, workers, rescuers, firefighters. Signals: 'загиблих', 'поранених', "
        "'постраждалих', 'є жертви', 'загинули', 'загинула', 'загинув', 'є загибла', "
        "'є загиблий', 'вбили', 'вбито', 'вбив', 'є жертва' in a Ukrainian context → REJECT.\n"
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
        "that supplies Ukraine (hurts Ukraine's supply) → REJECT.\n"
        "R9. Ukrainian domestic affairs: taxation, customs, VAT, budget laws, court "
        "rulings, corruption, social policy, price increases, economic burdens on "
        "Ukrainian citizens — any Ukrainian internal news that is not a military victory "
        "or foreign support → REJECT.\n"
        "R10. Statements, speeches, interviews, or proposals by Zelensky or any Ukrainian "
        "official — readiness to meet Putin, calls for ceasefire/truce/negotiations, "
        "energy truce proposals, summit participation — unless it is a confirmed delivery "
        "of foreign military or financial aid to Ukraine → REJECT.\n"
        "R11. Election polls, surveys, election results, or party standings in any country "
        "(Germany, France, USA, anywhere) → REJECT.\n"
        "R12. Putin or any Russian/Kremlin official making statements, threats, or "
        "declarations (threats to Europe, NATO, conditions, demands) — Russia speaking = "
        "Russia still in power, not positive → REJECT.\n"
        "R13. Any country, institution, or person blocking, delaying, refusing, or opposing "
        "support for Ukraine: frozen asset transfers blocked, aid delayed, sanctions relief "
        "for Russia, countries refusing to help → REJECT.\n"
        "R14. Ceasefire talks, peace negotiations, summits where Russia participates as an "
        "equal party (G20 with Putin, UN talks, etc.) → REJECT.\n\n"
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
