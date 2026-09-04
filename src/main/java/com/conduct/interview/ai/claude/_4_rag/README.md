# _4 — RAG (Retrieval-Augmented Generation)

Claude's knowledge is frozen at training cutoff and has no access to your data.  
RAG = inject relevant docs into the prompt at query time, Claude answers from those docs.

**Pattern:** query → retrieve top-k chunks → wrap in `<documents>` XML → send with "answer only from provided docs"

```python
# System: "Answer using only the provided documents."
# User message:
"""
<documents>
  <doc id="1">...chunk...</doc>
  <doc id="2">...chunk...</doc>
</documents>

Question: ...
"""
```

Count tokens before sending — retrieved context can blow the budget fast.  
Production uses embeddings + vector DB. Scripts here use in-memory Jaccard similarity (no deps).

## Scripts
| File | Demonstrates |
|---|---|
| `_common.py` | Shared: 6-doc knowledge base, `retrieve()`, `build_context()` — not runnable |
| `_1_basic_rag.py` | Retrieve → inject → generate |
| `_2_rag_with_streaming.py` | Retrieve → inject → stream answer live |
| `_3_rag_token_budget.py` | Token guard — how retrieved context grows input |

## Run
```bash
export ANTHROPIC_API_KEY=$(cat key.txt)
cd _4_rag && ../venv/bin/python _1_basic_rag.py
```
