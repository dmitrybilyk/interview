http://localhost:8001/

# Redis Developer Guide

## What is Redis?
**Redis** (Remote Dictionary Server) is an **in-memory** data structure store used as a database, cache, and message broker. It is designed for high-performance scenarios where sub-millisecond response times are required.



### Key Properties:
*   **Memory-First:** Data is stored in RAM for maximum speed.
*   **Persistence:** Supports RDB (snapshots) and AOF (logs) to save data to disk.
*   **Atomic Operations:** Every command is executed sequentially; no race conditions.
*   **Versatility:** Supports more than just strings (Hashes, Lists, Sets, etc.).

---

## 🛠 Redis Learning Roadmap

### 1. Basic Caching (Strings)
*   **Goal:** Store simple key-value pairs with expiration.
*   **Core Commands:** `SET`, `GET`, `SETEX` (Set with Expiry), `TTL`.
*   **Logic:** `GET key` -> if null -> `Fetch from DB` -> `SETEX key 3600 value`.

### 2. Object Management (Hashes)
*   **Goal:** Store objects with multiple fields without serializing a whole JSON string.
*   **Core Commands:** `HSET`, `HGET`, `HINCRBY`.
*   **Logic:** Useful for user sessions or profiles where you only need to update a single field like `last_login`.


### 3. Rate Limiting (Counters)
*   **Goal:** Limit the number of actions a user can perform in a specific timeframe.
*   **Core Commands:** `INCR`, `EXPIRE`.
*   **Logic:** Increment a key for a User ID. If the value exceeds the limit, block the request until the key expires.

### 4. Deduplication (Sets)
*   **Goal:** Store a collection of unique items.
*   **Core Commands:** `SADD`, `SCARD`, `SISMEMBER`.
*   **Logic:** Track unique visitor IDs or tags. Redis automatically discards duplicate entries.

### 5. Task Queues (Lists)
*   **Goal:** Implement a simple message queue.
*   **Core Commands:** `LPUSH` (Producer), `RPOP` or `BRPOP` (Consumer).
*   **Logic:** Move background tasks (like sending emails) from the main application to a worker process.


### 6. Real-time Rankings (Sorted Sets)
*   **Goal:** Maintain a list that is always sorted by a score.
*   **Core Commands:** `ZADD`, `ZINCRBY`, `ZREVRANGE`.
*   **Logic:** Ideal for leaderboards. The sorting happens inside Redis, making retrieval extremely fast.

### 7. Event Broadcasting (Pub/Sub)
*   **Goal:** Send messages to multiple listeners simultaneously.
*   **Core Commands:** `PUBLISH`, `SUBSCRIBE`.
*   **Logic:** Notify all running microservice instances to clear their local cache when a global update occurs.

### 8. Distributed Locking
*   **Goal:** Ensure only one process performs a specific action at a time.
*   **Core Commands:** `SET key value NX PX 30000`.
*   **Logic:** The `NX` (Not Exists) flag ensures only the first caller gets the lock, and `PX` (Expiry) prevents deadlocks if the caller crashes.

---

## 🚀 Recommended Practice Order
1.  **PSVM Phase:** Use the `Lettuce` or `Jedis` library in a simple Java `main` method to run these commands.
2.  **Visualization:** Use **Redis Insight** to monitor how keys appear and expire in real-time.
3.  **Spring Phase:** Move to `RedisTemplate` or `@Cacheable` once the raw commands are understood.