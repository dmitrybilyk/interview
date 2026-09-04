# Discussion topics — _2_prompting

- Zero-shot vs few-shot: when does adding examples actually hurt? Can too many examples bias the model toward the examples' pattern even when it doesn't fit?
- In `few_shot.py` the model invented `neu` — is that a failure of few-shot or evidence it understood the pattern? How do you decide which it is?
- Multi-shot covers all vocabulary — but what's the practical limit on how many examples you can send before you hit token/cost concerns?
- System prompt drift over long conversations — can you write a multi-turn loop that actually demonstrates the drift? What triggers it fastest?
- XML tags vs JSON vs plain colons for structuring prompts — is there evidence from the API that one format reliably outperforms others, or is it model-dependent?
- `output_constraint.py` — what constraints are available beyond `max_tokens`? Can `stop_sequences` act as a format constraint?
- In `structured_json_output_check.py`, the fence-stripping workaround is needed even with "no markdown" in the system prompt — is there a prompt wording that reliably prevents fences, or is post-processing always safer?
- What's the difference between putting rules in the system prompt vs the user message? Does position (system vs user) change how strongly the model follows them?
- `xml_structure.py` — if the user input itself contains XML tags, could that confuse the structure? How do you escape or isolate untrusted input?
- Prefill technique: starting the assistant turn with `{"` to force JSON — how does that interact with the system prompt's "no markdown" instruction?
