# _4 — RAG (Retrieval-Augmented Generation)

**Cert notes section:** Streaming, Context Management & RAG

## What this covers
| Concept | Detail |
|---|---|
| Retrieve | Find relevant document chunks for the query |
| Inject | Place chunks into the context as `<documents>` XML |
| Generate | Claude answers using only the provided docs |
| Stream | Stream the grounded answer live |
| Token budget | Count tokens before sending — retrieved context grows input fast |

## The RAG pattern
```
User query
    → retrieve top-k relevant chunks from doc store
    → build context: <documents>chunk1 chunk2</documents>
    → send to Claude: system + context + question
    → stream / return grounded answer
```

## Why RAG
Claude's knowledge has a training cutoff and no access to your private data.
RAG lets you inject fresh or proprietary content at query time — without fine-tuning.

## Key rules (cert)
- **Ground the answer** — system prompt: "Answer using only the provided documents"
- **XML-tag the docs** — `<doc id="...">text</doc>` separates chunks unambiguously
- **Count tokens first** — retrieved context adds to input; guard before sending
- **Chunk size matters** — too large → wastes tokens; too small → loses context

## In-memory store used here
Pure Python Jaccard similarity (no external deps).
Production RAG uses embeddings + vector DB (pgvector, Pinecone, Weaviate, etc.).

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: 6-doc Java knowledge base, `retrieve()`, `build_context()` — not runnable on its own |
| `_1_basic_rag.py` | Retrieve → inject → generate |
| `_2_rag_with_streaming.py` | Retrieve → inject → stream answer live |
| `_3_rag_token_budget.py` | Token counting guard — how retrieved context grows input tokens |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _4_rag && python _1_basic_rag.py   # or any other file in this directory
```
