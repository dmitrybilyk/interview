"""
Topic: Prompt caching — cache_control, cache hits, cost reduction up to 90%
Cert notes section: "2 API features reduce what you pay: Prompt caching, Token counting"
         "A single character change invalidates the cache"
Run: ../venv/bin/python script.py
"""

import os, time
from anthropic import Anthropic

client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

# Pricing reference (haiku-4-5, per million tokens, approximate):
# Normal input:       $0.80 / M
# Cache write:        $0.96 / M  (slightly more than normal to create cache)
# Cache read:         $0.08 / M  (10x cheaper — the 90% saving)

# ── A large system prompt that is expensive to process every time ──────────
LARGE_SYSTEM = """You are a senior software architect with deep expertise in:

1. Distributed systems design — CAP theorem, eventual consistency, SAGA pattern,
   event sourcing, CQRS, two-phase commit, Raft consensus, vector clocks.

2. API design — REST, gRPC, GraphQL, OpenAPI spec, versioning strategies,
   idempotency keys, rate limiting, pagination patterns.

3. Database internals — B-tree vs LSM-tree, MVCC, WAL, index types (B-tree,
   hash, GIN, GiST, partial, covering), query planning, EXPLAIN output.

4. Cloud-native architecture — Kubernetes, Helm, service mesh (Istio),
   container security (RBAC, network policies, pod security standards),
   multi-region deployments, disaster recovery, RTO/RPO.

5. Security — OWASP Top 10, threat modelling (STRIDE), OAuth2/OIDC flows,
   JWT internals, secrets management (Vault), mTLS, zero-trust networking.

Always give precise, opinionated answers backed by trade-offs.
Format your answers with clear sections when the question is complex.
""" * 3   # multiply to make it large enough to be cache-worthy (>1024 tokens)

QUESTION = "What is the main trade-off between B-tree and LSM-tree indexes?"

# ── 1. First call — cache is created (cache_creation_input_tokens) ─────────
print("=== 1. First call — cache MISS (cache being written) ===")
t0 = time.time()
r1 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    system=[{
        "type": "text",
        "text": LARGE_SYSTEM,
        "cache_control": {"type": "ephemeral"}  # mark this block for caching
    }],
    messages=[{"role": "user", "content": QUESTION}],
)
t1 = time.time()

u1 = r1.usage
print(f"  Latency: {t1-t0:.2f}s")
print(f"  input_tokens:             {u1.input_tokens}")
print(f"  cache_creation_tokens:    {getattr(u1, 'cache_creation_input_tokens', 0)}")
print(f"  cache_read_tokens:        {getattr(u1, 'cache_read_input_tokens', 0)}")
print(f"  Answer: {r1.content[0].text[:100]}...")

# ── 2. Second call — same system prompt → cache HIT ───────────────────────
print("\n=== 2. Second call — cache HIT (cache_read_input_tokens) ===")
t2 = time.time()
r2 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=256,
    system=[{
        "type": "text",
        "text": LARGE_SYSTEM,       # MUST be byte-for-byte identical
        "cache_control": {"type": "ephemeral"}
    }],
    messages=[{"role": "user", "content": "What is MVCC and why is it useful?"}],
)
t3 = time.time()

u2 = r2.usage
print(f"  Latency: {t3-t2:.2f}s")
print(f"  input_tokens:             {u2.input_tokens}")
print(f"  cache_creation_tokens:    {getattr(u2, 'cache_creation_input_tokens', 0)}")
print(f"  cache_read_tokens:        {getattr(u2, 'cache_read_input_tokens', 0)}")
print(f"  Answer: {r2.content[0].text[:100]}...")

# ── 3. Cache invalidation — ANY change kills the cache ────────────────────
print("\n=== 3. Cache invalidation (one character changed) ===")
MODIFIED_SYSTEM = LARGE_SYSTEM + "."   # one character appended

r3 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=64,
    system=[{
        "type": "text",
        "text": MODIFIED_SYSTEM,   # different → cache miss again
        "cache_control": {"type": "ephemeral"}
    }],
    messages=[{"role": "user", "content": "Hi"}],
)
u3 = r3.usage
print(f"  cache_creation_tokens: {getattr(u3, 'cache_creation_input_tokens', 0)}  ← re-created")
print(f"  cache_read_tokens:     {getattr(u3, 'cache_read_input_tokens', 0)}")

# ── 4. Caching tool definitions ───────────────────────────────────────────
print("\n=== 4. Caching tool list (same pattern) ===")
tools = [
    {
        "name": "query_db",
        "description": "Query the database for records.",
        "input_schema": {"type": "object", "properties": {"sql": {"type": "string"}}, "required": ["sql"]},
        "cache_control": {"type": "ephemeral"}   # cache_control on the LAST tool
    }
]
r4 = client.messages.create(
    model="claude-haiku-4-5-20251001",
    max_tokens=64,
    tools=tools,
    messages=[{"role": "user", "content": "What tools do you have?"}],
)
print(f"  Tool list cached. cache_creation: {getattr(r4.usage, 'cache_creation_input_tokens', 0)}")

print("""
KEY TAKEAWAYS (cert notes):
- cache_control: ephemeral  →  mark a content block for caching
- Cache TTL is 5 minutes (refreshed on each cache hit)
- Cache write costs slightly more; cache read is ~10x cheaper
- One character change anywhere BEFORE the cached block invalidates it
- Cache the static parts: system prompt, tool list, large documents
- The dynamic part (user messages) goes AFTER cached blocks
""")
