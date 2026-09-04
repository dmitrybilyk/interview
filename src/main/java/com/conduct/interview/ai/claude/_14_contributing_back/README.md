# _14 — Contributing Back (picking the right channel)

This one isn't an API feature, it's a decision-making topic — no script fits
it, it's exam material. Once you've packaged something for your own team
(see `_13_packaging_for_reuse`), this is about the extra steps to hand it to
people outside your team without it dying in a queue.

## The one-sentence version

Send the right-sized thing to the right place, make it so a total stranger
can check it actually works without asking you anything, and confirm you're
even allowed to share it — check that last one *first*, not last.

## Step 0 — are you allowed to share this? (always check this first)

If the code came out of a customer project, two things before anything
else:

- You actually have the right to give this code away (check the contract/
  engagement terms).
- If you built on someone else's work, you say so (attribution).

If you can't clear this, don't share it. Hand it to whoever owns that
decision instead of pushing it through yourself.

## Step 1 — which channel does this go to?

| What you built | Where it goes |
|---|---|
| A small tool that does one thing (e.g. wraps one API call in a clean function) | **The tool's own repo.** Not the Cookbook — the Cookbook isn't where standalone tools live. |
| A full app — UI, deployment scripts, multiple moving parts | **Nowhere, as-is.** Strip it down to just the one reusable pattern inside it. *Then* that pattern can go to the Cookbook. |
| A fix or improvement to something that already exists in the Cookbook | **That example's own repo/PR.** You're patching something that exists, not submitting something new. |

Why the app doesn't just go to the Cookbook: the Cookbook reviews one
focused pattern at a time. Hand a reviewer a whole app and there's no way
for them to review it quickly — it just sits there.

## Step 2 — can a stranger check this works without asking you?

Before you submit anywhere, you need all four of these, or it sits at the
back of the queue:

1. **It does one thing.** Not five things bundled in one file.
2. **There's an example that shows it running.** Not a description of what
   it does — an actual example a reviewer can run.
3. **There's a test that proves it works.** So the reviewer doesn't have to
   take your word for it or re-run your reasoning themselves.
4. **You wrote down what it assumes.** What environment it expects, what
   input format, what it needs set up beforehand. Otherwise the first time
   it breaks for someone else, it becomes the maintainer's problem to
   figure out why.

## The story that explains why this matters

A developer's code worked great — he used it every day. He opened a pull
request. Three weeks went by with no review. He asked what the holdup was.

The maintainer said: it probably does work, I just can't tell. There's no
test I can run, no example proving the behavior, and nothing saying what it
assumes about the environment. A PR I have to reverse-engineer goes to the
back of the queue until I have time to do that.

The developer added an example, a test, and a short note on assumptions.
It got reviewed fast.

**The lesson:** it was never about whether the code was good. It was about
whether someone else could check that it's good without redoing the
developer's thinking for him. The example, the test, and the assumptions
are obvious to you because you already have the context. They are not
obvious to anyone else.

## Worked examples (this is the exam-style bit)

**Case A** — a small function that wraps one API call, and that's it.
→ Goes to: **the tool's own repo.**
→ What's missing: **a test.** Right now it's just a function — nothing
proves it behaves correctly.

**Case B** — a full customer-service app: UI, deployment scripts, the
works, submitted as one piece.
→ Goes to: **nowhere yet.** Has to be cut down to just the one reusable
pattern first — then that piece can go to the Cookbook.
→ What's missing: **reducing it to one focused pattern.** A whole app
doesn't fit a review built for one thing.

**Case C** — a one-line fix to an existing Cookbook example, and that line
came out of a customer engagement.
→ Goes to: **that example's own repo/PR.** You're patching something that
already exists, not submitting something new.
→ What's missing: **the rights check.** Since the fix came out of client
work, you have to confirm you're allowed to give that line away before
anyone even looks at whether it's a good fix.

## Quick memory hook for the exam

- **Size decides the channel.** Tool → its own repo. Full app → shrink
  first, then Cookbook. Fix to an existing example → that example's repo.
- **Four things make it reviewable:** one thing, a running example, a
  test, stated assumptions.
- **Rights come before all of it.** No amount of good code fixes a
  licensing problem.
