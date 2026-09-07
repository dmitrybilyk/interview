# Redis as Message Broker — Theory & Patterns

## What is Redis (as a broker)?

Redis is primarily an **in-memory data store**, but it supports two messaging patterns
that make it usable as a lightweight broker:

- **Pub/Sub** — fire-and-forget broadcast. No persistence. Publisher → all active subscribers.
- **Streams** — persistent, append-only log with consumer groups, similar to a lightweight Kafka.

Redis is not a replacement for Kafka or RabbitMQ in high-load systems, but it shines when
you already have Redis in your stack and need simple messaging without adding new infrastructure.

```
Pub/Sub:  Publisher → PUBLISH channel → all active Subscribers (no storage)
Streams:  XADD stream → persisted entry → Consumer Group (XREADGROUP + XACK)
```

**When to choose Redis for messaging:**
- You already use Redis for caching — simple pub/sub for cache invalidation is free.
- Low-volume event delivery without broker overhead.
- Lightweight job queue (streams + consumer groups as a mini Kafka).
- Real-time notifications within a single datacenter.

**Not a good fit when:** you need guaranteed delivery at scale, complex routing, or exactly-once
semantics — use Kafka or RabbitMQ instead.

---

## Quick Start

```bash
cd message_brokers/redis
docker-compose up -d
# Redis Insight UI → http://localhost:5540
```

---

## Two Messaging Modes

### 1. Pub/Sub — fire-and-forget

```
Publisher → PUBLISH channel msg → Subscriber (all active)
```

- Messages are **not stored** — subscribers must be online to receive.
- **At-most-once** delivery.
- No consumer groups, no ack, no replay.
- Use: live notifications, chat, real-time dashboards, cache invalidation.
- Demo: `_1_PubSubDemo`

### 2. Streams — persistent log (like a lightweight Kafka)

```
XADD orders-stream * orderId 1 amount 100
```

- Messages are **persisted** in the stream (append-only log).
- Consumer groups: multiple consumers share work, each message delivered to one.
- Acknowledgement via `XACK` — until ACK, message stays in the **Pending Entries List (PEL)**.
- Can replay by reading from a past ID.
- Demo: `_2_StreamsAtLeastOnce`, `_3_StreamsDlqDemo`

---

## Delivery Guarantees

### Pub/Sub: At-Most-Once only
No persistence, no ack, no retry. Loss is expected.

### Streams: At-Least-Once
```
XREADGROUP GROUP g CONSUMER c COUNT 10 BLOCK 1000 STREAMS orders-stream >
↓
process(msg)
XACK orders-stream g <message-id>   ← only after successful processing
```
- `>` means "give me new (undelivered) messages"
- If consumer crashes before XACK → message stays in PEL → redelivered on next poll / claim

### Streams: No native Exactly-Once
Use idempotent writes (dedup by message ID in DB) to approximate it.

---

## Dead-Letter Queue (DLQ) via PEL

Redis has **no built-in DLQ**. Implement via Pending Entries List:

```
Fail to process → don't XACK → message stays in PEL
                                      ↓
                   XAUTOCLAIM (after idle threshold) → retry
                                      ↓
                   Max retries reached → XADD to orders-dlq-stream + XACK original
```

Key commands:
- `XPENDING stream group - + 10` — list pending messages
- `XAUTOCLAIM stream group consumer minIdle start count` — claim stuck messages
- `XCLAIM stream group consumer minIdle id` — manual claim (older API)

Demo: `_3_StreamsDlqDemo`

---

## Streams vs Kafka vs RabbitMQ

| | Redis Streams | Kafka | RabbitMQ |
|---|---|---|---|
| Persistence | Yes (in-memory+AOF/RDB) | Yes (disk) | Until ACK |
| Consumer groups | Yes | Yes | No (competing consumers) |
| Ordering | Per-stream | Per-partition | Per-queue |
| DLQ | Manual (PEL+claim) | Manual (produce to topic) | Native (DLX) |
| Exactly-once | No | Yes (transactions) | No |
| Max throughput | Very high (in-memory) | Very high | High |
| Use case | Cache+messaging combo | High-volume event log | Complex routing |

---

## Key Stream Commands

```bash
# Add message
XADD orders-stream * orderId 1 amount 100

# Create consumer group
XGROUP CREATE orders-stream orders-group $ MKSTREAM

# Read as consumer
XREADGROUP GROUP orders-group consumer-1 COUNT 5 BLOCK 2000 STREAMS orders-stream >

# Acknowledge
XACK orders-stream orders-group <message-id>

# List pending
XPENDING orders-stream orders-group - + 10

# Claim stuck (idle > 5000ms)
XAUTOCLAIM orders-stream orders-group recovery-consumer 5000 0-0 COUNT 10

# Read stream (replay from beginning)
XRANGE orders-stream - +
```
