"""
Topic: RAG token budget — retrieved context grows input tokens; guard before sending
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python _3_rag_token_budget.py
"""

from _common import client, retrieve, build_context, DOCS

MAX_INPUT_TOKENS = 600

query = "Explain ExecutorService and thread pools in Java."

print("=== RAG token budget guard ===")

for top_k in [1, 2, 3, 6]:
    docs = retrieve(query, top_k=top_k)
    context = build_context(docs)
    messages = [{
        "role": "user",
        "content": f"<documents>\n{context}\n</documents>\n\nQuestion: {query}",
    }]

    count = client.messages.count_tokens(
        model="claude-haiku-4-5-20251001",
        system="Answer using only the provided documents. Be concise.",
        messages=messages,
    )
    fits = count.input_tokens <= MAX_INPUT_TOKENS
    print(f"top_k={top_k}  docs={[d['id'] for d in docs]}  "
          f"tokens={count.input_tokens}  fits={fits}")
    if not fits:
        print(f"  → would exceed {MAX_INPUT_TOKENS} token budget — reduce top_k or chunk size")
        break
