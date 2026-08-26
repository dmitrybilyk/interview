# Discussion topics — _1_basic_call

- `basic_call.py` makes one request and reads `r.content[0].text` — why is `content` a list and not just a string? When would it have more than one element?
- What happens if `max_tokens` is set lower than the response actually needs? Does the API error or just cut off?
- `max_tokens_cutoff.py` — how do you detect programmatically that the response was cut (finish_reason)? What are all possible finish_reason values?
- Temperature is a float 0.0–1.0 but the API accepts up to 1.0 — is there a hard cap? What breaks above 1.0?
- From the temperature comparison we ran: two runs at temp=0.0 — are they guaranteed to be byte-for-byte identical? What about across model versions?
- Context window is input + output — if I set max_tokens=200k on Haiku, does the API accept it even if there's no room for output?
- What's the difference between `max_tokens` (hard cap) and `max_tokens` in practice — does the model always try to use all of them?
- Why does `"Hi " * 500` not cost exactly 500× more than `"Hi"`? What's the tokenization doing to repeated words?
- `stop_sequences` parameter — how does it interact with `max_tokens`? Which one wins?
- If I call the API with the same message at temp=0, will cache hits give the same result as non-cached calls?
