# RabbitMQ — Theory & Delivery Strategies

## What is RabbitMQ?

RabbitMQ is a **smart message broker** — it routes, buffers, and delivers messages between services
using the AMQP protocol. Unlike Kafka (a dumb log), RabbitMQ actively manages message lifecycle:
routing, delivery confirmation, TTL, and dead-lettering are all handled by the broker.

```
Producer → Exchange → (routing rules) → Queue → Consumer
```

The central concept is the **Exchange**: it decides which queue(s) a message goes to based on
routing key and exchange type (direct / topic / fanout / headers).
Messages are **deleted from the queue after ACK** — not stored long-term.

**When to choose RabbitMQ:**
- Complex routing logic (different queues by event type, priority, region).
- Task queues (work distribution among competing workers).
- Low-latency delivery with per-message TTL and DLX.
- Traditional pub/sub within your infrastructure.

**Not a good fit when:** you need event replay (Kafka), millions of messages/sec throughput,
or multiple independent consumer groups reading the same data.

---

## Quick Start

```bash
cd message_brokers/rabbitmq
docker-compose up -d
# Management UI → http://localhost:15672  (guest / guest)
```

Run order: `_0_InfraSetup` → any demo class.

---

## Architecture

```
Producer → Exchange → (binding rules) → Queue → Consumer
```

RabbitMQ is a **Smart Broker** — it manages routing, delivery, TTL, and retries.
Unlike Kafka, messages are **deleted after acknowledgement**.

---

## Exchange Types

### Direct — exact routing key match
```
Exchange(direct) + binding("order") → orders-queue
Producer.publish(key="order") → goes to orders-queue
Producer.publish(key="payment") → dropped (no matching binding)
```
- Use: targeted task routing (process order, send invoice, etc.)
- Demo: `_1_DirectExchangeDemo`

### Topic — wildcard routing key
```
*  = exactly one word
#  = zero or more words

binding("logs.*")   matches: logs.info, logs.error
                    no match: logs.app.info (two words after logs.)

binding("logs.#")   matches: logs.info, logs.app.info, logs.a.b.c
```
- Use: geographic routing, severity filtering, multi-level categorization
- Demo: `_2_TopicExchangeDemo`

### Fanout — broadcast to ALL queues, ignores routing key
```
Exchange(fanout) → fanout-q-1
                 → fanout-q-2
                 → fanout-q-3
```
- Use: pub/sub, cache invalidation, config broadcast, audit logging
- Demo: `_3_FanoutExchangeDemo`

### Headers — routes on message header attributes instead of routing key
- Rarely used in practice; use Topic/Direct for 99% of cases.

---

## Delivery Guarantees

### At-Most-Once
```java
// auto-ack=true (or commit before processing)
ch.basicConsume(queue, true, callback, cancel); // auto-ack
```
- Message ACKed immediately on delivery → processing failure → **message lost**
- Fastest, simplest
- Use: metrics, logging where loss is acceptable

### At-Least-Once
```java
ch.basicQos(1);
ch.basicConsume(queue, false, (tag, msg) -> {
    process(msg);
    ch.basicAck(msg.getEnvelope().getDeliveryTag(), false); // ACK after processing
}, cancel);
```
- ACK after successful processing → crash → **message redelivered**
- Consumer must be **idempotent**
- Use: orders, payments, notifications — default choice

### No native Exactly-Once
RabbitMQ does not support exactly-once. To approximate it:
- Idempotent consumers (dedup by message ID stored in DB)
- Outbox pattern (write to DB + publish in one DB transaction)

### Strategy Comparison

| | At-Most-Once | At-Least-Once |
|---|---|---|
| Message loss | Possible | No |
| Duplicates | No | Possible |
| Performance | Fastest | Fast |
| Use case | Analytics | Most business flows |

---

## Dead-Letter Exchange (DLX)

RabbitMQ's **native DLQ mechanism** — automatic, no manual routing code needed.

A message enters the DLX when:
1. `basicNack(requeue=false)` — consumer explicitly rejects
2. Message TTL expires
3. Queue length limit reached

```java
// Wire DLX when declaring the queue:
Map<String, Object> args = new HashMap<>();
args.put("x-dead-letter-exchange",    "my-dlx");
args.put("x-dead-letter-routing-key", "dead");
channel.queueDeclare("orders-queue", true, false, false, args);
```

**Difference from Kafka DLQ**: RabbitMQ DLX is declared at the queue level — it happens
automatically on nack/TTL/overflow. In Kafka you must manually produce to the DLQ topic.

Demo: `_4_DeliveryGuaranteesDemo` — msg-5 is nack(requeue=false) → goes to dead-letters queue.

---

## Fair Dispatch (basicQos)

Without `basicQos`: RabbitMQ round-robins messages to consumers upfront (greedy prefetch).
A slow consumer gets half the backlog while a fast one is idle.

```java
channel.basicQos(1);  // "Give me one message at a time"
```

With `basicQos(1)`: a consumer only gets the next message after ACKing the previous one.
Fast workers naturally handle more messages. Demo: `_5_FairDispatchScaling`.

---

## Key Concepts Cheat Sheet

| Concept | Description |
|---|---|
| **basicAck** | Success — message deleted from queue |
| **basicNack(requeue=true)** | Failure — message returned to queue head for retry |
| **basicNack(requeue=false)** | Poison — message goes to DLX or discarded |
| **basicQos(1)** | Fair dispatch — one unacked message per consumer |
| **Durable queue** | Survives broker restart (persist=true on queue+message) |
| **TTL** | `x-message-ttl`: message expires if unconsumed within N ms |
| **DLX** | `x-dead-letter-exchange`: where rejected/expired messages go |

---

## Vs Kafka Summary

| | Kafka | RabbitMQ |
|---|---|---|
| Model | Distributed log (pull) | Smart broker (push) |
| Message retention | Configurable (default 7d) | Deleted after ACK |
| Ordering | Per-partition | Per-queue (single consumer) |
| Replay | Yes (seek offset) | No (consumed = gone) |
| DLQ | Manual (produce to DLQ topic) | Native (DLX at queue level) |
| Exactly-once | Yes (transactions) | No |
| Scale | Very high throughput | High throughput, flexible routing |
