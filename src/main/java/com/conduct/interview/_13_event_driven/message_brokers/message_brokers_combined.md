# Message Brokers — Complete Guide

---

## What Is a Message Broker?

A message broker is middleware that decouples producers (senders) from consumers (receivers).
Producers publish messages without knowing who consumes them. Consumers receive messages without
knowing who produced them. The broker handles delivery, buffering, routing, and guarantees.

```
Without broker:  ServiceA.call(ServiceB)   → tight coupling, ServiceB must be up
With broker:     ServiceA → [broker] → ServiceB  → loose coupling, async, resilient
```

Three main choices covered here:

| | Kafka | RabbitMQ | Redis |
|---|---|---|---|
| Type | Distributed event log | Smart message broker | In-memory store + messaging |
| Model | Pull (consumer reads) | Push (broker delivers) | Pull (Streams) / Push (Pub/Sub) |
| Persistence | Yes (disk, configurable retention) | Until ACK | In-memory + optional AOF/RDB |
| Message replay | Yes (seek to any offset) | No | Yes (Streams) / No (Pub/Sub) |
| Native DLQ | No (manual) | Yes (DLX) | No (manual via PEL) |
| Exactly-once | Yes (Kafka Transactions) | No | No |
| Routing | By partition key | By exchange type + routing key | By channel / stream key |
| Best for | High-throughput event log, pipelines | Complex routing, task queues | Cache + simple messaging combo |

---

# Kafka

## What is Kafka?

Apache Kafka is a **distributed event streaming platform** — a high-throughput, fault-tolerant,
persistent log that decouples producers from consumers.

Unlike a traditional message queue, Kafka doesn't delete messages after they're read.
It stores them in an **ordered, immutable log** per partition. Multiple independent consumer groups
can each read the same data at their own pace.

```
Producer → [Topic: orders (3 partitions)] → Consumer Group A (billing)
                                           → Consumer Group B (shipping)
                                           → Consumer Group C (analytics)
```

**When to choose Kafka:**
- High throughput (millions of messages/sec).
- Multiple consumers need the same event stream independently.
- Events must be replayable (seek to any past offset).
- Exactly-once semantics required.

**Not a good fit when:** you need complex routing logic, per-message TTL, or request-reply patterns.

## Core Concepts

| Concept | Description |
|---|---|
| **Topic** | Logical channel. Messages NOT deleted after read — kept for retention period (default 7d). |
| **Partition** | Physical shard. Ordering guaranteed *within* a partition. More partitions = more parallelism. |
| **Offset** | Sequential ID of a message inside a partition. Consumer's bookmark. |
| **Producer** | Writes to topics. Same key → same partition (hash-based). |
| **Consumer** | Reads from topics. Tracks its own offset per partition. |
| **Consumer Group** | Each partition owned by exactly one consumer in the group. Enables horizontal scaling. |
| **Broker** | A Kafka server node. Cluster = multiple brokers. |
| **Commit** | Consumer saying "I've processed up to this offset." |

## Delivery Guarantees

### At-Most-Once
```
Poll → COMMIT offset → Process
```
- Commit **before** processing. Crash during processing → **message lost** (already committed).
- Use for: metrics, analytics where occasional loss is acceptable.

### At-Least-Once
```
Poll → Process → COMMIT offset
```
- Commit **after** processing. Crash → **message redelivered** on restart.
- May produce duplicates → consumer must be **idempotent**.
- Use for: orders, payments, notifications — most business flows.

### Exactly-Once
```
BEGIN TX → Process → Write output → commitOffsets inside TX → COMMIT TX
```
- Producer: `enable.idempotence=true` + `transactional.id`
- Consumer: `isolation.level=read_committed`
- Offset committed **atomically** with output write inside Kafka transaction.
- Crash → transaction aborted, nothing written, input reprocessed cleanly.
- Only works Kafka→Kafka. ~2-3× overhead vs at-least-once.
- Use for: financial aggregations, stream processing.

### Comparison

| | At-Most-Once | At-Least-Once | Exactly-Once |
|---|---|---|---|
| Message loss | Possible | No | No |
| Duplicates | No | Possible | No |
| Performance | Fastest | Fast | ~2-3× slower |
| Use case | Analytics | Most business | Finance/aggregation |

## Dead-Letter Queue (DLQ)

Kafka has **no native DLQ** — implement manually:

```
orders-topic ──[process OK]──→ commitSync
             ──[process FAIL]──→ publish to orders-dlq + commitSync
```

1. On failure: publish poison message to `orders-dlq` with error headers.
2. Commit offset anyway — main topic moves forward, no infinite retry loop.
3. Separate DLQ consumer monitors for alerting / manual reprocessing.

**Key:** always commit after routing to DLQ — otherwise endless retry loop on the same message.

## Consumer Group Scaling

```
orders-topic (3 partitions)
    partition-0  →  consumer-A
    partition-1  →  consumer-B
    partition-2  →  consumer-C
```

- Each partition owned by **exactly one consumer** per group.
- Kill consumer-A → Kafka **rebalances** (~10s), consumer-B takes partition-0.
- Add consumer-D → idles (can't exceed partition count).
- Same key → same partition → guaranteed ordering per entity.

## Key Settings

| Setting | Value | Effect |
|---|---|---|
| `acks` | `all` | All ISR confirm — safest, needed for exactly-once |
| `enable.idempotence` | `true` | No duplicate sends on producer retry |
| `transactional.id` | `"id"` | Enables Kafka transactions |
| `enable.auto.commit` | `false` | Manual offset commit control |
| `isolation.level` | `read_committed` | Skip uncommitted transactional data |

---

# RabbitMQ

## What is RabbitMQ?

RabbitMQ is a **smart message broker** — it routes, buffers, and delivers messages using AMQP.
Unlike Kafka (a dumb log), RabbitMQ actively manages message lifecycle: routing, TTL, DLX.

```
Producer → Exchange → (routing rules) → Queue → Consumer
```

Messages are **deleted after ACK** — not stored long-term.

**When to choose RabbitMQ:**
- Complex routing logic (by event type, priority, region).
- Task queues with fair work distribution.
- Per-message TTL and native dead-lettering.

**Not a good fit when:** you need event replay, millions of messages/sec, or independent consumer groups.

## Exchange Types

### Direct — exact routing key match
```
binding("order") → orders-queue
publish(key="order")   → ✓ orders-queue
publish(key="payment") → ✗ dropped
```
Use: targeted task routing.

### Topic — wildcard routing key
```
*  = exactly one word       binding("logs.*")   → logs.info ✓  logs.app.info ✗
#  = zero or more words     binding("logs.#")   → logs.info ✓  logs.app.info ✓
```
Use: severity filtering, geographic routing, multi-level categorization.

### Fanout — broadcast to ALL bound queues (ignores routing key)
```
Exchange(fanout) → queue-1, queue-2, queue-3  (all receive every message)
```
Use: pub/sub, cache invalidation, config broadcast.

### Headers — routes on message headers (rarely used in practice).

## Delivery Guarantees

### At-Most-Once
```java
ch.basicConsume(queue, true, callback, cancel); // autoAck=true
```
ACKed on delivery. Crash during processing → **message lost**.

### At-Least-Once
```java
ch.basicQos(1);
ch.basicConsume(queue, false, (tag, msg) -> {
    process(msg);
    ch.basicAck(msg.getEnvelope().getDeliveryTag(), false); // ACK after processing
}, cancel);
```
Crash → **message redelivered**. Consumer must be idempotent.

### No native Exactly-Once
Approximate with: idempotent consumers (dedup by message ID in DB) or Outbox pattern.

## Dead-Letter Exchange (DLX) — Native DLQ

RabbitMQ's built-in dead-letter mechanism. A message enters DLX when:
1. `basicNack(requeue=false)` — consumer explicitly rejects (poison message).
2. Message TTL expires.
3. Queue length limit reached.

```java
Map<String, Object> args = new HashMap<>();
args.put("x-dead-letter-exchange",    "my-dlx");
args.put("x-dead-letter-routing-key", "dead");
channel.queueDeclare("orders-queue", true, false, false, args);
```

**vs Kafka DLQ:** RabbitMQ DLX is automatic at the queue level. Kafka DLQ requires manually producing to the DLQ topic.

## Fair Dispatch

Without `basicQos`: round-robin prefetch — slow consumer hogs messages, fast one starves.

```java
channel.basicQos(1);  // one at a time — next message only after ACK
```

With `basicQos(1)`: fast workers naturally handle more messages.

## Key Concepts

| Concept | Description |
|---|---|
| `basicAck` | Success — message deleted from queue |
| `basicNack(requeue=true)` | Failure — message returned to queue head for retry |
| `basicNack(requeue=false)` | Poison — routed to DLX or discarded |
| `basicQos(1)` | Fair dispatch — one unacked message per consumer |
| Durable queue | Survives broker restart |
| TTL | `x-message-ttl`: message expires if unconsumed within N ms |
| DLX | `x-dead-letter-exchange`: destination for rejected/expired messages |

---

# Redis

## What is Redis (as a broker)?

Redis is primarily an **in-memory data store** with two messaging modes:

- **Pub/Sub** — fire-and-forget broadcast. No persistence. At-most-once.
- **Streams** — persistent append-only log with consumer groups. At-least-once.

Use Redis for messaging when you already have it for caching and don't want to add broker infrastructure.

```
Pub/Sub:  PUBLISH channel msg → all active Subscribers (no storage)
Streams:  XADD stream * k v  → persisted → Consumer Group (XREADGROUP + XACK)
```

**When to choose Redis for messaging:**
- Already in stack — cache invalidation via pub/sub is zero-cost.
- Simple lightweight job queue without Kafka overhead.
- Real-time notifications at moderate scale.

## Pub/Sub — At-Most-Once

```
Publisher → PUBLISH channel → Subscriber (must be active)
```
- No storage — offline subscriber misses messages.
- No ack, no replay, no consumer groups.
- Use: live notifications, cache invalidation, dashboards.

## Streams — At-Least-Once

```
XREADGROUP GROUP g consumer COUNT 10 BLOCK 1000 STREAMS stream >
↓ process
XACK stream g <message-id>   ← commit after processing
```
- `>` = give me new (undelivered) messages.
- Crash before XACK → message stays in **Pending Entries List (PEL)** → redelivered.
- Multiple consumers in a group share work (each message to one consumer).
- Replay by reading from a specific past ID.

## Dead-Letter Queue via PEL

Redis has no built-in DLQ. Implement via PEL + XAUTOCLAIM:

```
fail → don't XACK → stays in PEL
                          ↓
        XAUTOCLAIM (idle > threshold) → retry
                          ↓
        max retries → XADD to dlq-stream + XACK original
```

```java
// Claim messages stuck in PEL for > 500ms
Map.Entry<StreamEntryID, List<StreamEntry>> claimed = jedis.xautoclaim(
    stream, group, consumer, 500L,
    StreamEntryID.MINIMUM_ID,
    XAutoClaimParams.xAutoClaimParams().count(10)
);
```

## Key Stream Commands

```bash
XADD stream * orderId 1 amount 100          # add message
XGROUP CREATE stream group $ MKSTREAM       # create consumer group
XREADGROUP GROUP g c COUNT 5 STREAMS s >   # read new messages
XACK stream group <id>                      # acknowledge
XPENDING stream group - + 10               # list pending (not acked)
XAUTOCLAIM stream group consumer 5000 0-0 COUNT 10  # claim stuck
XRANGE stream - +                          # replay from beginning
```

---

# DLQ Comparison

| Broker | Trigger | Mechanism | Setup |
|---|---|---|---|
| **Kafka** | Manual | Produce to `*-dlq` topic + commitSync | Producer code in consumer |
| **RabbitMQ** | Auto | `basicNack(requeue=false)` → DLX routes it | Queue declaration args |
| **Redis** | Manual | PEL + XAUTOCLAIM → XADD to dlq-stream | Claim loop code |

---

# Full Strategy Comparison

| | At-Most-Once | At-Least-Once | Exactly-Once |
|---|---|---|---|
| **Kafka** | Commit before process | Commit after process | Kafka Transactions |
| **RabbitMQ** | `autoAck=true` | `basicAck` after process | Not available |
| **Redis Streams** | — | `XACK` after process | Not available |
| **Redis Pub/Sub** | Always at-most-once | — | — |

---

# When to Use Which

| Need | Choose |
|---|---|
| High-throughput event log, multiple independent readers, replay | **Kafka** |
| Complex routing (by type/region/priority), task queues, native DLQ | **RabbitMQ** |
| Already have Redis, simple notifications or lightweight job queue | **Redis** |
| Cross-organizational event delivery (partners, SaaS) | **Webhooks** (HTTP) |
| In-service decoupling, no infrastructure | **Spring ApplicationEvents** |
