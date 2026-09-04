# _18 — Comparing Platforms: Latency, Compliance, Cost

No script — this is how you back up the platform choice from `_17` with
actual numbers, so it survives a procurement/security review instead of
just being "it's what I picked."

## Latency

Measure it from the customer's actual region, with their actual size of
request — not from your laptop. Your laptop hides the real network delay.

- A platform running in the customer's own cloud region = faster round trip.
- The first-party API usually gets new features first, before other
  platforms get them.
- On Bedrock specifically: global vs. regional endpoint changes both
  latency and cost — test both before deciding.

## Compliance — usually settles it on its own

If the customer already has a certification or a "data must stay in
country X" rule, that's often a hard pass/fail, not a tradeoff to weigh.

- First-party API might not cover EU residency — check
  platform.claude.com for current coverage.
- EU-only residency usually means Bedrock or Vertex AI.
- Microsoft Foundry: depends on the specific model — some run fully on
  Azure, some run on Anthropic's infrastructure and don't meet EU
  residency. Check per model with Microsoft.

Ask about this while scoping the project, not after the contract's signed.

## Cost — token price isn't the whole story

Token prices are similar across platforms. What actually moves the bill:
data transfer (egress), platform fees, and how much integration work it
takes. A cheaper token price can end up costing more overall.

Measure: total cost per call, not just price per token.

## The shortcut

If the customer's compliance requirement is already pass/fail (e.g. "must
be EU-only"), skip comparing all three — that one requirement already
picked the platform for you.

## What goes wrong if you skip this

A dev picked the platform the team already knew, because the deadline was
close and that migration was fast. It built fine, tests passed. Then at
the customer's security review: "where's the data processed?" — turns out
that platform didn't meet the customer's residency rule. A different
platform, one the team barely knew, would have passed easily. Had to
rebuild the whole integration on the right platform.

**The point:** easy for your team to build ≠ allowed for the customer to
run. Those are two different questions, and passing one tells you nothing
about the other. For a regulated customer, check residency/compliance
*during scoping*, before any code is written — catching it there costs a
conversation. Catching it at the review costs the whole rebuild.
