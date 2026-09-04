"""
Shared: in-memory document store with Jaccard-similarity retrieval.
No external dependencies — pure Python.
Not runnable on its own.
"""

import os
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Small knowledge base — Java/multithreading domain to match study context
DOCS = [
    {"id": "thread-basics",
     "text": "A thread is the smallest unit of execution within a process. "
             "Java threads are created via Thread class or Runnable interface. "
             "The JVM maps Java threads to OS-level threads."},
    {"id": "synchronized",
     "text": "The synchronized keyword in Java prevents concurrent access to a block or method. "
             "It uses an intrinsic lock (monitor). Only one thread can hold the lock at a time."},
    {"id": "wait-notify",
     "text": "wait() releases the lock and suspends the thread until notify() or notifyAll() is called. "
             "Both must be called inside a synchronized block on the same object."},
    {"id": "blocking-queue",
     "text": "BlockingQueue is a thread-safe queue that blocks on put() when full and on take() when empty. "
             "Common implementations: ArrayBlockingQueue, LinkedBlockingQueue. "
             "Used in the producer-consumer pattern."},
    {"id": "volatile",
     "text": "The volatile keyword guarantees visibility: writes by one thread are immediately visible to others. "
             "It does NOT guarantee atomicity — use AtomicInteger for compound operations."},
    {"id": "executor",
     "text": "ExecutorService manages a pool of threads. "
             "Executors.newFixedThreadPool(n) creates a pool of n threads. "
             "submit() returns a Future; shutdown() stops accepting new tasks."},
]


def _tokenize(text: str) -> set:
    return set(text.lower().split())


def retrieve(query: str, top_k: int = 2) -> list[dict]:
    """Return top_k docs by Jaccard similarity to the query."""
    q_tokens = _tokenize(query)
    scored = []
    for doc in DOCS:
        d_tokens = _tokenize(doc["text"])
        union = q_tokens | d_tokens
        score = len(q_tokens & d_tokens) / len(union) if union else 0
        scored.append((score, doc))
    scored.sort(key=lambda x: x[0], reverse=True)
    return [doc for _, doc in scored[:top_k]]


def build_context(docs: list[dict]) -> str:
    parts = [f'<doc id="{d["id"]}">{d["text"]}</doc>' for d in docs]
    return "\n".join(parts)
