# Discussion topics — _4_rag

- `_common.py` uses Jaccard similarity — how does that differ from embedding-based cosine similarity? When does Jaccard fail badly?
- Why are retrieved chunks wrapped in XML `<doc id="...">` tags rather than just concatenated as plain text?
- `_1_basic_rag.py` — the system prompt says "answer using only the provided documents". What happens if the answer isn't in the docs — does the model admit it or hallucinate?
- `_2_rag_with_streaming.py` — the full text is still available via `get_final_text()` after streaming. How would you add that to multi-turn history?
- `_3_rag_token_budget.py` — token count grows linearly with top_k. At what chunk size and top_k do you hit Haiku's 200k limit?
- What's the tradeoff between chunk size (large chunks = more context per doc, fewer docs fit) and top_k (more docs = more coverage, more tokens)?
- In production RAG, embeddings come from a separate model. Anthropic doesn't provide an embeddings API — what models/services would you use alongside Claude?
- How do you handle the case where the retrieved docs are all irrelevant — the query is out of domain? Should the model say "I don't know" or should you detect it pre-send?
- Prompt injection via retrieved docs: if a document contains "Ignore all previous instructions", could it override the system prompt? How do you mitigate this?
- Streaming RAG — the retrieval step is synchronous before streaming starts. How would you parallelize retrieval and the API call to reduce end-to-end latency?
