# Apache Kafka — Theory & Delivery Strategies

## What is Kafka?

Apache Kafka is a **distributed event streaming platform** — a high-throughput, fault-tolerant,
persistent log that decouples producers from consumers.

Unlike a traditional message queue (RabbitMQ), Kafka doesn't delete messages after they're read.
It stores them in an **ordered, immutable log** per partition. Multiple independent consumer groups
can each read the same data at their own pace. This makes Kafka ideal for event sourcing, audit logs,
stream processing, and data pipelines.

```
Producer → [Topic: orders (3 partitions)] → Consumer Group A (billing)
                                           → Consumer Group B (shipping)
                                           → Consumer Group C (analytics)
```

**When to choose Kafka:**
- High throughput (millions of messages/sec).
- Multiple consumers need the same event stream independently.
- Events must be replayable (seek to any past offset).
- Exactly-once semantics required (Kafka Transactions).

**Not a good fit when:** you need complex routing logic, per-message TTL, or request-reply patterns
(use RabbitMQ or HTTP instead).

---

## Quick Start

```bash
cd message_brokers/kafka
docker-compose up -d
# Kafka UI → http://localhost:8089
```

Run order: `_0_TopicSetup` → `_1_KeyedProducer` → any consumer.

---

## Core Concepts

| Concept | Description |
|---|---|
| **Topic** | Logical channel. Messages are NOT deleted after read — they stay for the retention period (default 7 days). |
| **Partition** | Physical shard of a topic. Ordering is guaranteed *within* a partition. More partitions = more parallelism. |
| **Offset** | Sequential ID of a message inside a partition. Think of it as a cursor/bookmark. |
| **Producer** | Writes to topics. Controls which partition via key hash (same key → same partition). |
| **Consumer** | Reads from topics. Tracks its own offset per partition. |
| **Consumer Group** | A team of consumers splitting work: each partition is owned by exactly one consumer in the group. Enables horizontal scaling. |
| **Broker** | A Kafka server node. A cluster = multiple brokers sharing partitions. |
| **Commit** | Consumer telling Kafka "I've processed up to this offset." |

---

## Delivery Guarantees

### At-Most-Once (`_2_AtMostOnceConsumer`)
```
Poll → COMMIT → Process
```
- Offset committed **before** processing.
- **Crash during processing → message is lost** (already committed).
- Use for: metrics, click tracking, anything where occasional loss is OK.

### At-Least-Once (`_3_AtLeastOnceConsumer`)
```
Poll → Process → COMMIT
```
- Offset committed **after** processing.
- **Crash during processing → message redelivered on restart.**
- May produce duplicates → consumer must be **idempotent** (dedup by ID, upsert, etc.).
- Use for: orders, payments, notifications — most business flows.

### Exactly-Once (`_4_ExactlyOnceProducerConsumer`)
```
BEGIN TX → Process → Write output → commitOffsets inside TX → COMMIT TX
```
- Producer: `enable.idempotence=true` + `transactional.id`
- Consumer: `isolation.level=read_committed` (skips uncommitted/aborted data)
- Offset commit happens **atomically inside the Kafka transaction** with the output write.
- **Crash → transaction is aborted, nothing written, input reprocessed cleanly.**
- Only works Kafka→Kafka (for DB writes you need Outbox/Saga pattern).
- ~2-3× throughput overhead vs at-least-once.
- Use for: financial aggregations, stream processing where duplicates break correctness.

### Strategy Comparison

| | At-Most-Once | At-Least-Once | Exactly-Once |
|---|---|---|---|
| Message loss | Possible | No | No |
| Duplicates | No | Possible | No |
| Performance | Fastest | Fast | ~2-3× slower |
| Complexity | Low | Low | High |
| Use case | Analytics | Most business | Finance/aggregation |

---

## Dead-Letter Queue (DLQ) Pattern (`_5_DlqProducerConsumer`)

Kafka has **no native DLQ** — you implement it manually:

```
orders-topic ──┬──[process OK]──→ commitSync
               └──[process FAIL]──→ orders-dlq + commitSync
```

1. Consumer reads from `orders-topic`.
2. On failure: publish poison message to `orders-dlq` with error headers.
3. Commit offset anyway — main topic moves forward, no infinite retry loop.
4. Separate DLQ consumer monitors for alerting / manual reprocessing.

**Key insight**: always commit after routing to DLQ, otherwise you get an endless loop retrying the same broken message.

---

## Consumer Group Scaling (`_6_ConsumerGroupScaling`)

```
orders-topic (3 partitions)
    partition-0  →  consumer-A
    partition-1  →  consumer-B
    partition-2  →  consumer-C
```

- Each partition is owned by **exactly one consumer** per group.
- Kill consumer-A → Kafka **rebalances** (~10s): consumer-B takes partition-0.
- Add consumer-D → it idles (can't exceed partition count).
- Ordering guarantee: messages with the same key always go to the same partition → processed in order by the same consumer.

---

## Key Producer Settings

| Setting | Value | Effect |
|---|---|---|
| `acks` | `0` | Fire and forget (fastest, at-most-once) |
| `acks` | `1` | Leader confirms (default) |
| `acks` | `all` | All ISR confirm (safest, needed for exactly-once) |
| `enable.idempotence` | `true` | No duplicate sends on retry |
| `transactional.id` | `"my-id"` | Enables transactions (exactly-once) |

## Key Consumer Settings

| Setting | Value | Effect |
|---|---|---|
| `enable.auto.commit` | `false` | Manual control of offset commits |
| `auto.offset.reset` | `earliest` | Read from beginning if no committed offset |
| `isolation.level` | `read_committed` | Skip uncommitted transactional data |
| `max.poll.records` | N | Batch size per poll |
