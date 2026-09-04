# _15 — From Business Problem to Requirements

No script, this is a planning step, not an API pattern. It's what you do
*before* picking a deployment platform, so the choice can be defended
later instead of just "it's what I know."

Turn a vague business ask into two lists.

## 1. Functional requirements — what it must do

Specific enough to test. "Help agents answer faster" isn't one. This is:

- Classify each ticket into one of 4 queues
- Draft a reply that cites the actual policy
- Never auto-send without a human approving it

Each line should be checkable — something you could write an eval for.

## 2. Infrastructure requirements — the constraints nobody said out loud

You get these by asking, not by re-reading the business ask:

| Ask | Question |
|---|---|
| Latency | How fast, measured where the user actually is? |
| Scale | How many requests, at peak? |
| Residency | Where must the data live, under which regulation? |
| Identity | Who acts, under what credentials, and what gets logged? |

These four usually end up deciding which platform you can even use.

## Write it down

A short record — the functional list, the infrastructure list, and which
regulation each constraint comes from — is what lets someone who wasn't in
the room review the platform decision later.

**Skip this** for a throwaway prototype nobody will review. Quick notes
are enough.

## Worked example

Business problem: "Help support agents answer faster."

**Functional:**
- Classify ticket into one of 4 queues
- Draft a reply citing the relevant policy doc
- Never send without human approval

**Infrastructure:**
| Constraint | Answer | Why |
|---|---|---|
| Latency | Reply drafted in <5s while agent reads the ticket | Agent is waiting live |
| Scale | ~2,000 tickets/day, peak 50/min during outages | Support volume |
| Residency | EU customer data stays in EU | GDPR |
| Identity | Agent's own SSO identity, every draft logged to the ticket | Audit trail for support QA |

## Files

- `requirements_template.md` — blank copy of the above, ready to fill in for a real project
