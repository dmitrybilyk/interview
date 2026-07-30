Common Coding Interview Questions (Implementation-Heavy)
1️⃣ Cache / Storage Design

Implement LRU Cache (Least Recently Used).

Implement LFU Cache (Least Frequently Used).

Implement Cache with TTL / Expiry (entries expire after given time).

Implement Write-Through Cache (sync with backing store).

Implement Two-Level Cache (memory + disk).

2️⃣ Rate Limiting / Retry / Throttling

Sliding Window Rate Limiter – allow only N requests per time window. DONE

Token Bucket Rate Limiter – refill tokens over time.

Fixed Window Counter – count requests per interval, reset each window.

Exponential Backoff Retry – retry failed calls with increasing delay.

Circuit Breaker – open circuit after repeated failures, half-open after cooldown.

Request Debouncing / Throttling – merge or limit rapid calls.

3️⃣ Data Structure Design

Implement Priority Queue / Min-Max Heap from scratch. IN_PROGRESS

Implement Disjoint Set / Union-Find with path compression.

Implement Thread-Safe Bounded Queue (producer-consumer).

Implement Concurrent HashMap with segment locking or CAS.

Implement Circular Buffer / Ring Buffer with overwrites.

Implement Bloom Filter for probabilistic membership checks.


4️⃣ Scheduling / Timing

Task Scheduler – schedule tasks with cooldown periods.

Delayed Job Queue – tasks become available after a delay.

Retry Queue with Backoff – store failed jobs, retry later.

5️⃣ Streaming / Sliding Windows

Moving Average of Last N Items in a data stream.

Median of Streaming Data – maintain median dynamically.

Top-K Frequent Elements in Stream – track top k efficiently.

6️⃣ Classic System/Algo Patterns

Implement Consistent Hash Ring – for server load balancing.

Design URL Shortener – short ID generation, collision handling.

Implement Session Store – sessions with TTL, cleanup.

Implement In-Memory Key-Value Store with persistence/logging.

7️⃣ Locking / Concurrency

Implement Read-Write Lock.

Implement Semaphore.

Implement Thread Pool / Executor.

8️⃣ Other Interview-Favorites

Producer-Consumer Problem – thread-safe solution.

Dining Philosophers Problem – concurrency challenge.

Deadlock Detection – implement a simple graph-based checker.

9️⃣ Variants of Rate-Limiting (Your Specific Case)

Sliding window with timestamps – 500 calls/minute.

Leaky bucket – smooth out bursts.

Token bucket – refill at rate R, max capacity C.