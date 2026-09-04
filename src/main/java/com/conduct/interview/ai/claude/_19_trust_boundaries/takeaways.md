# Key Takeaways — Packaging → Deploy (_13 through _19)

1. **Package while it's fresh.** Pull customer-specific stuff into config,
   write down the assumptions, bundle the eval + audit log. Do it now —
   whoever knows what's customer-specific won't be around later to ask.

2. **A maintainer only accepts what they can verify.** Right channel for
   the size (tool → its repo, big app → shrink first, fix → the existing
   repo), then: one focused thing, a runnable example, a test, stated
   assumptions. Rights/licensing checked *before* any of that.

3. **Pin what ships.** An alias is a moving target; a pinned model ID is a
   fixed snapshot. Pin it, keep the last known-good one — so an upstream
   model change is something you choose, not something that surprises you
   in prod with no way back.

4. **Measure the thing that actually decides it.** Latency: from the
   customer's real region. Compliance: against what they're already
   certified for — usually pass/fail, not a tradeoff. Cost: total per
   call, not the token price. Compliance is what usually ends the debate.

5. **Mark every seam as a boundary.** A multi-part app is only as safe as
   its worst-scoped piece. Anything crossing from one component to
   another gets treated as data, never as instructions — no matter how
   trustworthy the component that sent it was on its own.

## The one-line version of each

- Package → configure, don't rewrite.
- Contribute → verifiable, not just working.
- Deploy → pinned, not moving.
- Compare → measured, not assumed.
- Connect → every seam checked, none assumed safe.

## What this all adds up to

Full arc: a working build → a reusable asset → something you can hand to
another team or contribute publicly → placed on the right platform,
pinned → defensible under a procurement/security review → safe to wire
into a bigger app.

## Where to check current details (things that change over time)

- platform.claude.com — model IDs, versioning, deprecations, regional
  coverage
- Anthropic Trust Center — ZDR / HIPAA BAA eligibility
- Anthropic Cookbook (GitHub) — contribution conventions

## Key terms (alphabetical, quick recall)

- **Accelerator** — a working build packaged so the next team configures
  it instead of rebuilding it: customer-specific values as parameters,
  assumptions written down, eval bundled in.
- **Contribution readiness** — what a maintainer checks before accepting
  code: one focused thing, a runnable example, a test, stated assumptions
  — plus confirmed rights to give the code away.
- **Deployment platform** — where the workload actually runs. Six of
  them: first-party Claude API, Claude Platform on AWS, Claude in Amazon
  Bedrock, Bedrock (legacy), Google Vertex AI, third-party platforms.
  Same model, different identity/residency/latency/cost per platform.
- **Model alias vs. pinned ID** — an alias (`opus`, `sonnet`) points at
  "whatever's current" and can change under you; a pinned full ID is a
  fixed snapshot. Pin it so an upstream change is a choice, not a
  surprise.
- **Trust boundary** — the seam where data/instructions cross from one
  component to another. Whatever crosses is untrusted on arrival — treat
  it as data, never as instructions.
