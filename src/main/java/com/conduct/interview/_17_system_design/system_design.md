# System Design

Key concepts and trade-offs for senior interviews. Not about frameworks — about decisions.

---

## CAP Theorem

A distributed system can guarantee only **2 of 3**:

| Property | Meaning |
|---|---|
| **C**onsistency | Every read returns the most recent write (or an error) |
| **A**vailability | Every request gets a response (no error, may be stale) |
| **P**artition tolerance | System keeps working despite network partition between nodes |

**P is non-negotiable** (networks always can partition). So the real choice is **CP vs AP**:

- **CP** (consistent + partition tolerant): Returns error during partition rather than stale data. Example: HBase, Zookeeper, etcd.
- **AP** (available + partition tolerant): Returns possibly stale data rather than an error. Example: Cassandra, DynamoDB (default), CouchDB.
- **CA** (consistent + available): Only possible with no partitions = single node. Example: traditional RDBMS (PostgreSQL, MySQL) on one server.

**PACELC extension** (more practical): Even without partition, trade-off between Latency and Consistency.

---

## Consistency Models

From weakest to strongest:

| Model | Meaning | Example |
|---|---|---|
| **Eventual** | Replicas converge eventually. Reads may be stale. | DynamoDB, DNS |
| **Read-your-writes** | You always see your own writes. | Facebook timeline |
| **Monotonic reads** | Never see older data than you saw before. | Cassandra tuned |
| **Causal** | Causally related events seen in order by all. | CosmosDB |
| **Strong** | Every read sees the latest write. | PostgreSQL, Zookeeper |

Most distributed systems default to eventual consistency. Strong consistency costs throughput.

---

## Scaling Patterns

### Horizontal vs Vertical
- **Vertical** (scale up): bigger machine. Simple, limited, single point of failure.
- **Horizontal** (scale out): more machines. Complex, unlimited, needs statelessness.

### Stateless services
Keep state in DB/cache, not in memory. Any instance can serve any request. Enables horizontal scaling + rolling deploys.

### Caching

```
Client → [Cache] → DB
```

**Cache-aside (lazy)**: app checks cache → miss → load from DB → populate cache.
**Write-through**: write to cache + DB simultaneously. Always consistent, higher write latency.
**Write-behind (write-back)**: write cache immediately, async flush to DB. Fast writes, risk of loss.
**Read-through**: cache sits in front, fetches from DB automatically on miss.

Cache eviction policies: LRU (most common), LFU, TTL-based.

### Database Scaling
- **Read replicas**: route reads to replicas, writes to primary.
- **Sharding**: split data across DBs by key (user_id % N). Hard to rebalance. Avoid cross-shard joins.
- **Connection pooling**: don't open a DB connection per request — reuse from pool (HikariCP default max=10).

---

## Load Balancing

Distributes traffic across instances.

**Algorithms**: round-robin, least connections, IP hash (sticky sessions), consistent hashing.

**Layer 4** (TCP): fast, dumb, routes by IP:port.
**Layer 7** (HTTP): smart, routes by URL/header/cookie. Enables path-based routing, A/B.

**Health checks**: load balancer probes `/health` — removes unhealthy instances automatically.

---

## Rate Limiting Strategies

**Fixed window**: count requests per minute. Simple. Burst at window boundary.
**Sliding window**: count over rolling period. Accurate, higher memory.
**Token bucket**: tokens refill at rate R. Burst up to bucket size B. Smooth. Used by most APIs.
**Leaky bucket**: queue + drain at fixed rate. Strict smoothing, no bursts.

---

## API Gateway

Single entry point. Handles: auth, rate limiting, routing, SSL termination, request/response transform.

```
Client → API Gateway → Service A
                     → Service B
                     → Service C
```

Avoids each service reimplementing auth, rate limiting, observability.

---

## CDN

Serve static assets (images, JS, CSS) from edge nodes close to users.
Reduces latency (ms → low ms), offloads origin server.
Dynamic content: some CDNs support edge computing (CloudFront Functions, Cloudflare Workers).

---

## Interview Points

- Never say "just use a bigger machine" — explain when horizontal scaling is needed.
- CAP: always acknowledge P, explain CP vs AP choice based on the domain (banking = CP, social feed = AP).
- Caching reduces DB load but introduces staleness — always state your invalidation strategy.
- Sharding is a last resort — try read replicas and connection pooling first.
- Idempotency keys are mandatory for distributed writes (prevent duplicates on retry).
- Design for failure: assume any network call can fail, any node can crash.
