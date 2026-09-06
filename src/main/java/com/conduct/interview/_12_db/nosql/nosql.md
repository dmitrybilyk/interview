# NoSQL — Theory & Overview

---

## What is NoSQL
"Not Only SQL" — databases that don't use the relational tabular model.
Built to handle massive scale, flexible schemas, or data shapes that relational models handle poorly.

### Why it emerged
- Relational databases scale **vertically** (bigger machine). NoSQL scales **horizontally** (more machines).
- Web-scale companies (Google, Amazon, Facebook) needed to distribute data across thousands of cheap servers.
- Agile development — rigid schema migrations are slow; flexible schemas let the model evolve.

---

## ACID vs BASE

| | **ACID** (SQL) | **BASE** (NoSQL) |
|---|---|---|
| Philosophy | Strict correctness | Availability over consistency |
| **A** | Atomicity | **B**asically Available |
| **C** | Consistency | **S**oft state |
| **I** | Isolation | **E**ventually consistent |
| **D** | Durability | |
| Trade-off | Harder to scale horizontally | May return stale data |

**Eventual consistency**: all nodes will converge to the same state — eventually. Reads may return slightly stale data after a write.

---

## CAP Theorem

A distributed system can guarantee only **2 of 3**:

| Property | Meaning |
|---|---|
| **C**onsistency | Every read returns the most recent write (or an error) |
| **A**vailability | Every request gets a response (no error), but may be stale |
| **P**artition tolerance | System works despite network failures between nodes |

Network partitions always happen in real distributed systems, so **P is non-negotiable**.
The real choice is **CP** (consistent, may be unavailable) vs **AP** (always available, may be stale).

| Database | CAP posture |
|---|---|
| MongoDB | CP (by default with majority write concern) |
| Cassandra | AP (tunable consistency) |
| DynamoDB | AP (eventually consistent by default, strongly consistent option) |
| Redis (cluster) | AP |
| HBase | CP |
| PostgreSQL (single node) | CA (no partition) |

---

## Types of NoSQL Databases

### 1. Document Store
Stores JSON/BSON documents. Flexible schema. Rich querying.

**Best for:** User profiles, product catalogs, content management, any entity with variable attributes.

**Examples:** MongoDB, Couchbase, Firestore

```json
{
  "_id": "u123",
  "name": "Alice",
  "address": { "city": "Kyiv", "zip": "01001" },
  "tags": ["vip", "active"]
}
```

### 2. Key-Value Store
Simplest model. Data accessed only by key. Extremely fast.

**Best for:** Session storage, caching, shopping carts, feature flags, rate limiting.

**Examples:** Redis, DynamoDB (KV mode), Memcached

```
GET session:abc123  →  {"userId": 42, "expires": 1720000000}
```

### 3. Wide-Column (Column-Family) Store
Rows have a primary key, but each row can have different columns. Columns grouped into families.
Optimized for queries by partition key — excellent for time-series, event logs.

**Best for:** IoT sensor data, write-heavy time-series, large-scale analytics.

**Examples:** Apache Cassandra, HBase, Google Bigtable

```
RowKey: user123#2024-06
  column: events → [{ts:..., type:"click"}, ...]
  column: metadata → {country:"UA"}
```

### 4. Graph Database
Data modeled as nodes (entities) and edges (relationships). Traversing relationships is cheap.

**Best for:** Social networks, recommendation engines, fraud detection, knowledge graphs.

**Examples:** Neo4j, Amazon Neptune, JanusGraph

```
(Alice)-[:FOLLOWS]->(Bob)-[:LIKES]->(Post#42)
```

---

## When to choose NoSQL over SQL

| Choose NoSQL when... | Choose SQL when... |
|---|---|
| Schema changes constantly | Schema is stable and well-defined |
| Need horizontal scale (millions of writes/s) | ACID transactions are required |
| Data is naturally hierarchical / document-shaped | Complex joins and ad-hoc queries needed |
| Need sub-millisecond latency at scale | Reporting, analytics, aggregations |
| No complex relationships between entities | Strong referential integrity required |
| Geographic distribution / multi-region active-active | Single region, strong consistency |

---

## MongoDB
Document store. Rich querying and aggregation. Replica sets for HA. Sharding for horizontal scale.
See: [mongo.md](mongodb/mongo.md) for full detail.

**One-liners:**
- `_id` is always indexed (ObjectId by default).
- Embedding = fast reads; Referencing = flexibility + avoids 16 MB doc limit.
- Aggregation pipeline: `$match` → `$group` → `$project` → `$lookup`.
- Replica set: 1 Primary + 2 Secondaries. Automatic failover election.
- Sharding splits data by **shard key** — choose for even distribution (avoid hot spots).

---

## Redis
In-memory key-value store. Sub-millisecond reads/writes. Optional persistence.

**Data structures:**
| Type | Commands | Use case |
|---|---|---|
| String | GET, SET, INCR, EXPIRE | Cache, counters, rate limiting |
| Hash | HGET, HSET, HGETALL | Object fields (user profile) |
| List | LPUSH, RPOP, LRANGE | Message queues, activity feeds |
| Set | SADD, SMEMBERS, SINTER | Unique visitors, tags, friends |
| Sorted Set | ZADD, ZRANK, ZRANGE | Leaderboards, priority queues |
| Stream | XADD, XREAD | Event log, pub/sub with persistence |

**Key patterns:**
```bash
# Cache-aside
GET product:123              # miss → query DB → SET product:123 "{...}" EX 300

# Rate limiting (atomic)
INCR rate:user:42
EXPIRE rate:user:42 60       # or use MULTI/EXEC

# Distributed lock
SET lock:resource uuid NX PX 30000   # NX = only if not exists, PX = TTL ms
DEL lock:resource                     # release

# Leaderboard
ZADD leaderboard 1500 "alice"
ZADD leaderboard 2300 "bob"
ZREVRANGE leaderboard 0 9 WITHSCORES  # top 10
```

**HA modes:**
- Sentinel — monitors primary, promotes replica on failure.
- Cluster — sharded, 16384 hash slots across nodes, each with replicas.

**Java:** Spring Data Redis with Lettuce (async, thread-safe). `@Cacheable` / `@CacheEvict`.

**Interview points:**
- Redis is NOT a primary database — data fits in RAM; persistence (RDB/AOF) is optional but lossy.
- TTL on every cache key — unbounded keys cause OOM and eviction.
- Redis is single-threaded for commands → atomic by design (INCR is safe).

---

## Cassandra
Wide-column store. Masterless (all nodes equal), tunable consistency, extremely write-optimized.
Designed for massive write throughput across multiple regions.

**Architecture:**
- No master node — every node is equal (peer-to-peer, gossip protocol).
- Data is partitioned by **partition key** and distributed using consistent hashing.
- Replication factor (RF) = how many copies of each partition.
- Consistency level (ONE / QUORUM / ALL) — how many replicas must respond.
  - **Quorum** = `(RF/2)+1` — balance between consistency and availability.

**Data model — design for your queries:**
```sql
-- Table designed for "get all messages in a chat room, newest first"
CREATE TABLE messages (
    room_id  UUID,
    sent_at  TIMESTAMP,
    user_id  UUID,
    body     TEXT,
    PRIMARY KEY ((room_id), sent_at)  -- partition key = room_id, clustering = sent_at
) WITH CLUSTERING ORDER BY (sent_at DESC);
```
- No JOINs. No aggregations across partitions. No ad-hoc queries.
- Design tables **per query pattern** — denormalize deliberately.
- One table = one query. Multiple tables for multiple access patterns.

**Write path:** Write to commit log + MemTable → flush to SSTables. Compaction merges SSTables.
Delete = tombstone (logical delete). Actual removal happens at compaction.

**Strengths:**
- Millions of writes/second, linear scale-out.
- Multi-region active-active (each DC has its own consistency level).
- No single point of failure.

**Weaknesses:**
- No JOINs, no transactions across partitions, limited query flexibility.
- Tombstones accumulate → compaction can degrade performance.
- Schema design mistakes are expensive to fix at scale.

**Java:** DataStax Java Driver. Spring Data Cassandra.

---

## Comparison Table

| | PostgreSQL | MongoDB | Redis | Cassandra | DynamoDB |
|---|---|---|---|---|---|
| Type | Relational | Document | Key-Value | Wide-Column | KV + Document |
| Schema | Strict | Flexible | None | Fixed (per table) | Flexible |
| Scaling | Vertical | Horizontal (sharding) | Cluster | Horizontal | Serverless |
| Consistency | ACID | Tunable (majority) | Eventual (cluster) | Tunable (quorum) | Eventual / Strong |
| Joins | Yes | `$lookup` (limited) | No | No | No |
| Transactions | Full ACID | Multi-doc (v4+) | MULTI/EXEC | Lightweight (v4+) | Transactions |
| Best for | Complex queries, finance | Documents, catalogs | Cache, sessions | IoT, logs, messaging | Serverless, AWS native |
