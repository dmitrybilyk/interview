# _16 — Systems Lifecycle for Claude Apps

No script — this names the arc that `_15` through the rest of the module
sit inside. Same lifecycle as any engineered system, mapped onto a Claude
app:

1. **Requirements** — functional + infrastructure needs (`_15`)
2. **Design** — pick the platform, the model, the trust boundaries
3. **Build** — the agent, tools, prompts
4. **Test** — evals, unit, integration, end-to-end
5. **Deploy** — pin the version, gate promotion on the eval (`_13`)
6. **Operate** — watch cost, latency, errors; enforce guardrails
7. **Iterate** — production findings feed back into requirements

## The part that actually matters: gates

A gate = you don't move to the next phase until a condition is met. Two
examples from this course already:

- Design → Build: don't start building until the platform actually meets
  the residency requirement.
- Deploy → full production: don't roll out a new version until it clears
  the eval against the pinned baseline (`_13_packaging_for_reuse`).

Skipping a gate under deadline pressure is exactly how you get the
`_13`-style postmortem: it ran, so it looked done, and nobody checked
before the mistake mattered.

**When it's fine to skip phases:** a one-off experiment. **When it's not:**
a regulated deployment — that's what gates are for.
