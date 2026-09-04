"""
Topic: RAG with streaming — retrieve docs then stream the grounded answer live
Cert notes section: Streaming, Context Management & RAG
Run: ../venv/bin/python _2_rag_with_streaming.py
"""

from _common import client, retrieve, build_context

query = "What is the difference between volatile and synchronized in Java?"

docs = retrieve(query, top_k=2)
context = build_context(docs)

print("=== RAG + Streaming ===")
print(f"Query: {query}")
print(f"Retrieved docs: {[d['id'] for d in docs]}\n")
print("Answer: ", end="", flush=True)

with client.messages.stream(
    model="claude-haiku-4-5-20251001",
    max_tokens=128,
    system="Answer using only the provided documents. Be concise.",
    messages=[{
        "role": "user",
        "content": f"<documents>\n{context}\n</documents>\n\nQuestion: {query}",
    }],
) as stream:
    for chunk in stream.text_stream:
        print(chunk, end="", flush=True)

full = stream.get_final_text()
print(f"\n\nTokens used: {stream.get_final_message().usage.input_tokens} input, "
      f"{stream.get_final_message().usage.output_tokens} output")
