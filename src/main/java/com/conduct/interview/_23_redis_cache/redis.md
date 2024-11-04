# Redis: Key Concepts

Redis is an in-memory data store used as a database, cache, and message broker.

## Main Data Structures
- **Strings**: Simple values, up to 512MB.
- **Lists**: Ordered collections, good for queues.
- **Sets**: Unique, unordered collections.
- **Sorted Sets**: Sets with scores, ideal for leaderboards.
- **Hashes**: Key-value pairs, great for storing objects.
- **Bitmaps & HyperLogLogs**: Used for analytics and counting unique elements.

## Core Features
- **Persistence**: Supports snapshots (RDB) and operation logs (AOF).
- **Replication & Clustering**: Master-slave replication, Sentinel for high
  availability, and Redis Cluster for sharding.
- **Transactions**: Atomic execution with `MULTI` and `EXEC`.
- **Lua Scripting**: Runs scripts atomically on the server.
- **Pub/Sub**: Messaging via channels for real-time updates.
- **Eviction Policies**: Manages memory with strategies like LRU.

## Common Uses
- **Caching**: Fast, in-memory storage.
- **Session Store**: Handles web session data.
- **Real-Time Analytics**: Data tracking with sets and sorted sets.
- **Message Queues**: Queuing with lists and Pub/Sub.

Redis combines speed with flexibility, making it ideal for low-latency,
real-time applications.
