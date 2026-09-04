# _19 — Trust Boundaries in Multi-Component Apps

No script — this is what to check when you wire several Claude pieces
together (e.g. an API that triggers a Claude Code task, which then calls
an MCP server into a customer system).

## The core idea

Every place two components connect is a seam. At each seam, data or
instructions cross from one thing to another — and that's exactly where
identity, secrets, or untrusted input can leak through. Map every seam
before you connect anything.

## Rule 1 — don't trust content just because it worked before

If a Claude Code task fetches something off the internet, that content is
untrusted the moment it reaches the next component. Treat it as data, not
as instructions — same rule as prompt injection, just applied between
components instead of within one prompt.

## Rule 2 — least privilege, for the whole app, not per-piece

Give each component only the access its own job needs. The catch: the
whole app is only as safe as its most over-permissioned piece. One
component with too much access is a weak point even if every other
component is scoped perfectly.

## The map

| Component | Does what | Untrusted thing at its seam | Fix |
|---|---|---|---|
| API entry point | Kicks off the workflow | The request coming in from outside | Validate input, check identity |
| Claude Code task | Does the agent work, may fetch content | Anything it fetched from outside | Treat fetched content as data, not instructions |
| MCP server | Reaches into a customer system | The access it holds on the app's behalf | Scope it tight, log what it does |

## For a regulated customer

Bedrock and Vertex AI are usually the ones that meet regional residency
rules. Check ZDR / HIPAA BAA eligibility per component before you scope
anything — don't assume it carries over from one component to the next.

## If a seam can't be made safe

Don't ship it anyway. Escalate to a human owner instead.

## What goes wrong if you skip this

Two devs wire up three components. Each one already passed its own tests,
so it feels safe to just connect them. Dev B asks: where does the content
the Claude Code task fetched go? Straight into the next call, as part of
the prompt — "it's just content from the customer page." Dev B: that
content is untrusted. If it contains instructions, the next component will
just run them, because nobody marked that connection point as a boundary.

**The point:** each part passing its own tests tells you nothing about the
seam between them. "Trusted on its own" ≠ "safe to hand off untreated."
The seam nobody marks as a boundary is exactly the one that gets crossed.
