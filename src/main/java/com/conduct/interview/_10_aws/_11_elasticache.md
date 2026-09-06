# ElastiCache — Managed In-Memory Cache

## What it is
Managed Redis or Memcached. Sub-millisecond reads. Sits in front of your database to absorb repetitive queries.

## Redis vs Memcached
| | Redis | Memcached |
|---|---|---|
| Data types | Strings, lists, sets, sorted sets, hashes, streams, geo | Strings only |
| Persistence | Optional (RDB snapshots, AOF log) | No |
| Pub/Sub | Yes | No |
| Clustering | Yes (Redis Cluster, automatic sharding) | Yes (simpler) |
| Lua scripting | Yes | No |
| **Choose** | Almost always — richer, production-grade | Only if you need pure cache with simplest possible setup |

## Common use cases for Java developers
| Pattern | How |
|---|---|
| **Cache-aside** | App checks Redis → miss → query DB → store in Redis with TTL |
| **Session store** | Spring Session + Redis — store HTTP sessions outside the JVM (survives restart, works for multiple instances) |
| **Rate limiting** | `INCR key` + `EXPIRE` — atomic counter per user/IP per window |
| **Distributed lock** | `SET key value NX PX 30000` — acquire lock; `DEL key` — release |
| **Pub/Sub** | Publish events from one service, subscribe in another (simple; prefer SQS/SNS for reliability) |
| **Leaderboard** | Redis Sorted Set (`ZADD`, `ZRANK`, `ZRANGE`) — O(log n) |

## Java integration (Spring Boot)
```yaml
# application.yml
spring:
  data:
    redis:
      host: my-cluster.cache.amazonaws.com
      port: 6379
```
```java
@Cacheable(value = "products", key = "#id")   // Spring Cache abstraction → Redis
public Product findById(Long id) { return repo.findById(id).orElseThrow(); }

@CacheEvict(value = "products", key = "#product.id")
public void save(Product product) { repo.save(product); }
```
Spring Data Redis / Lettuce (default, async, thread-safe) or Jedis (sync, pool-based).

## Cluster modes
- **Single node** — dev/test only. No HA.
- **Redis with replica** — primary + 1–5 replicas. Failover in ~1 min.
- **Redis Cluster** — sharded across 1–500 nodes. Each shard has primary + replicas. Auto-scales data. Required for > ~200 GB or > ~200 000 req/s.

## ElastiCache Serverless
New option (2023): fully managed, auto-scales capacity. No cluster sizing needed. Pay per ECU (ElastiCache Unit) + GB stored.

## Security
- Deploy in private subnet. Never expose to internet.
- In-transit TLS: enable on cluster + use `rediss://` URI.
- At-rest encryption: enable for compliance.
- Auth token (Redis AUTH) or IAM auth (newer).

## Interview points
- ElastiCache is NOT durable by default — Redis can lose data on failover (AOF/RDB mitigates but adds latency).
- Use TTL on every key — unbounded keys fill memory and cause evictions (OOM errors).
- Eviction policy: `allkeys-lru` (evict least-recently-used when full) is the safest default for a cache.
- ElastiCache is only accessible within VPC — Lambda must be in same VPC to connect.
- Redis Cluster restricts multi-key operations to same slot — Lettuce handles this with `SlottingPolicy`.
