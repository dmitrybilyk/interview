# _13 — Packaging for Reuse (Accelerators)

The earlier modules built something that works: an agent loop, tool
definitions, a small test script. This module is about turning that into
something the *next* project can just reuse, instead of rebuilding it from
scratch every time.

The idea in one sentence: split the part that never changes from the part
that changes per customer, put the changing part in a config file with
sensible defaults, and write down what a new person needs to know that the
code itself doesn't say.

**Three kinds of thing you'd package, and what each one needs:**

| What you're packaging | What's inside it | What you need to do to make it reusable |
|---|---|---|
| Agent Template | The system prompt, the tool definitions, the loop that runs them | Move anything that changes per customer (prompts, limits, thresholds) into a config file with good defaults, so the next person edits a file instead of the code |
| MCP Server | The tools it exposes and what each one needs as input | Write down exactly what each tool takes as input, and let whoever installs it choose which tools are turned on — without touching the code |
| Eval Suite | The test questions and the rules for grading the answers | Package the test questions and the grading rules together, so a new team can run them and check the thing still works in their setup. Also: before you push a new AI model version live, run this same test set against it and compare the score to your last known-good score. If it's worse, don't ship it. |

**Write down what the code can't tell you:** what environment it expects to
run in, what input it expects, what problems it already handles, and the
test that proves it's actually working. Without this, the next person just
stares at the code and has to guess, or gives up and rewrites the whole
thing.

**Always keep a record of:** what data it touched, what account or identity
it acted as, and a log of what it actually did. Any customer with
compliance/security requirements will ask for exactly this, on day one.

**Skip all of the above** if this is a genuine one-off nobody will ever use
again — the extra work only pays off once you reuse it a second time.

## Scripts

| File | What it shows | Needs API key |
|---|---|---|
| `_pkg.py` | Shared code: loads `config.default.json` (plus an optional override file) and writes to `audit.log`. Not meant to be run by itself. |
| `_1_parameterize_agent_template.py` | Same shopping agent as `_12`, but now the model name, how many steps it can take, which actions need approval, and any extra instructions all come from a config file instead of being hardcoded | Yes |
| `_2_document_mcp_server_manifest.py` | Takes the same tool list from `_12` and turns it into a document that lists what each tool does and needs — pick which tools are turned on with `--scope`, no code changes | Yes* |
| `_3_eval_gate.py` | Runs a small set of test questions against the config-driven agent, checks it called the right tools in the right order and asked for approval when it should have, and fails loudly if the score drops below the last known-good score | Yes |

\* `_2` never actually calls the API, but it borrows the tool list from
`_12_agent_loop/_common.py`, and just importing that file happens to create
an Anthropic client too — so you still need the key set, for no real
reason. That's a good example of the kind of hidden coupling worth writing
down when you document something you're packaging. More on this in
`claude_discussions.md`.

## Run

```bash
export ANTHROPIC_API_KEY=$(cat ../key.txt)

python3 _1_parameterize_agent_template.py                            # with default settings
python3 _1_parameterize_agent_template.py acme_customer.example.json # with a customer override

python3 _2_document_mcp_server_manifest.py --scope read_only

python3 _3_eval_gate.py                                               # pass/fail check before you'd ship
```

## Files

- `config.default.json` — the default settings for the agent, written down in one place
- `acme_customer.example.json` — an example of a customer-specific override; only lists what's different from the defaults
- `eval_dataset.json` — the test questions
- `eval_baseline.json` — the score this thing needs to keep hitting
- `audit.log` — gets created the first time you run anything; every action gets one line in here

There's no separate manifest file for the MCP part — `_2` builds it fresh
from the real tool list every time it runs, so it can never go out of sync
with the actual tools.
