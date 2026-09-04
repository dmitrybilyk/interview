# _17 — Where It Runs, and Pinning the Version

No script here — nothing to run. Two decisions to write down: which cloud
to deploy to, and how to lock the model version so it can't move on you.

## Where it runs

Usually just: wherever the customer already runs their stuff — their
cloud, their login system, their compliance paperwork. Not "which platform
is technically best."

| Platform | Pick it when |
|---|---|
| First-party Claude API | No cloud requirement, want the newest features first |
| Claude Platform on AWS | Customer's on AWS, wants Anthropic's own model IDs |
| Claude in Amazon Bedrock | Customer's on AWS, data must stay inside their AWS account |
| Bedrock (legacy) | Already using the older InvokeModel/Converse setup |
| Google Vertex AI | Customer's on Google Cloud |
| Third-party (e.g. Microsoft Foundry) | Customer already uses that product |

Whoever's login system and data boundary you're on — that's the platform's
job, not something your code decides.

## How to actually tell them apart

They all run the same Claude models. The only thing that changes is
**whose servers the request runs on, and whose login it uses.** Ask two
questions:

1. **Does the data have to physically stay inside a specific cloud?**
   - Must stay in AWS → **Claude in Amazon Bedrock**
   - Must stay in Google Cloud → **Google Vertex AI**
   - No such rule → any of the others are fine
2. **Is there already a cloud/product the customer is locked into, even
   without a hard residency rule?**
   - They're on AWS, don't need strict residency, just want it to feel
     native in their AWS account → **Claude Platform on AWS** (this one's
     a bit of a hybrid: you access it through AWS, but Anthropic still
     runs the actual model, not AWS)
   - They already use some other product that happens to embed Claude,
     like Microsoft's tools → **that third-party platform** (e.g. Foundry)
   - None of the above, no constraints → **first-party Claude API**
     (Anthropic's own service — usually gets new features first)

One-line versions to memorize:
- **First-party API** = talking to Anthropic directly.
- **Bedrock** = fully inside the customer's AWS account — AWS identity, AWS
  data boundary, AWS bill.
- **Bedrock (legacy)** = same as Bedrock, just the older API style
  (InvokeModel/Converse instead of Messages). Only relevant if a customer
  is already stuck on it.
- **Vertex AI** = same idea as Bedrock, but for Google Cloud instead of AWS.
- **Platform on AWS** = accessed through AWS, but the model still runs on
  Anthropic's own infrastructure — not the same as Bedrock's "data never
  leaves AWS" guarantee.
- **Third-party (Foundry, etc.)** = Claude living inside someone else's
  product that the customer already uses.

## Worked example (checkpoint-style)

*A customer runs AWS, has a data-residency requirement, and needs to be
able to roll back a model update.*

- Platform → **Amazon Bedrock** (residency requirement + AWS = data has to
  stay inside their AWS account, which is exactly what Bedrock is for)
- Identity → **AWS identity** (comes with Bedrock — not an Anthropic key)
- Model reference → **a pinned full model ID** (never a moving alias —
  they need to control exactly when it changes)
- Rollback → **retain the prior pinned version** (that's what makes
  rollback possible at all)

## Pin the version — don't use a name that can move

There are two ways to write a model name: a name like `"opus"` that always
points at "whatever's current," or an exact snapshot that never changes on
its own.

```python
model = "claude-haiku-4-5"             # can silently change under you
model = "claude-haiku-4-5-20251001"    # locked — only changes if you edit this line
```

Always use the locked one in anything real. Keep the previous locked
version around too, so if the new one breaks something you can just switch
back to it.

## What happens if you don't — a real-shaped incident

```
deploy: model="opus"  status=ok
alias moved to a new opus version — nobody touched the app
parser: KeyError "summary" in response
error: response shape changed, parsing broke
tried to roll back — no old version saved, nothing to go back to
had to patch the parser as an emergency fix
```

Nothing in the app changed. The name `"opus"` just started pointing at a
different model, and the response came back shaped differently than the
code expected. Because nobody had kept the old version pinned anywhere,
there was no quick "switch it back" — just an emergency patch.

## How to not end up in that log

- Use the exact model name, never the moving one.
- Keep the last known-good model name written down somewhere, so rolling
  back is one line, not a scramble.
- Before switching to a new version, run it through your test set first
  and check nothing broke — catch it in a test, not in production.

**Skip all of this** for a throwaway prototype that never goes to
production. This is for stuff that actually ships.
