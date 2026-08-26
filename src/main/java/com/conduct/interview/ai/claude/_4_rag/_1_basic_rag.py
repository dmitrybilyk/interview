"""
Topic: Basic RAG — retrieve relevant chunks, inject into context, generate answer
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python _1_basic_rag.py
"""

from _common import client, retrieve, build_context

query = "How does wait() and notify() work in Java?"

docs = retrieve(query, top_k=2)
context = build_context(docs)

print("=== Basic RAG ===")
print(f"Query: {query}")
print(f"Retrieved docs: {[d['id'] for d in docs]}\n")

r = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    system="Answer using only the provided documents. Be concise.",
    messages=[{
        "role": "user",
        "content": f"<documents>\n{context}\n</documents>\n\nQuestion: {query}",
    }],
)
print("Answer:", r.content[0].text.strip())
