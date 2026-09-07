# Message Brokers — Overview

Runnable Java examples (no Spring, plain clients) for Kafka, RabbitMQ, and Redis.
Each folder has its own `docker-compose.yml` — start it, run the Java classes from IntelliJ.

---

## Folder Structure

```
message_brokers/
├── kafka/
│   ├── docker-compose.yml       ← Kafka + Zookeeper + Kafka-UI (port 8089)
│   ├── kafka.md                 ← Theory + strategy comparison
│   └── examples/
│       ├── _0_TopicSetup               Run first — creates all topics
│       ├── _1_KeyedProducer            Produces to orders-topic (3 partitions, keyed)
│       ├── _2_AtMostOnceConsumer       Commit before process → fast but lossy
│       ├── _3_AtLeastOnceConsumer      Commit after process → safe, possible duplicates
│       ├── _4_ExactlyOnceProducerConsumer  Kafka transactions → no loss, no duplicates
│       ├── _5_DlqProducerConsumer      Poison messages → orders-dlq topic
│       └── _6_ConsumerGroupScaling     3 consumers / 3 partitions, watch rebalance
│
├── rabbitmq/
│   ├── docker-compose.yml       ← RabbitMQ + Management UI (port 15672)
│   ├── rabbitmq.md              ← Theory + exchange types + delivery comparison
│   └── examples/
│       ├── _0_InfraSetup               Run first — creates all exchanges/queues/DLX
│       ├── _1_DirectExchangeDemo       Exact routing key match
│       ├── _2_TopicExchangeDemo        Wildcard routing (logs.*)
│       ├── _3_FanoutExchangeDemo       Broadcast pub/sub
│       ├── _4_DeliveryGuaranteesDemo   ack / nack(requeue) / nack(DLX)
│       └── _5_FairDispatchScaling      basicQos(1) fast vs slow worker
│
└── redis/
    ├── docker-compose.yml       ← Redis + Redis Insight UI (port 5540)
    ├── redis.md                 ← Theory + pub/sub vs streams comparison
    └── examples/
        ├── _1_PubSubDemo               Fire-and-forget pub/sub (at-most-once)
        ├── _2_StreamsAtLeastOnce       Consumer groups with XACK (at-least-once)
        └── _3_StreamsDlqDemo           PEL + XAUTOCLAIM → dead-letter stream
```

---

## Delivery Strategy Quick Reference

| Strategy | Kafka | RabbitMQ | Redis Streams |
|---|---|---|---|
| **At-Most-Once** | Commit before process | `autoAck=true` | Pub/Sub |
| **At-Least-Once** | Commit after process | `basicAck` after process | `XACK` after process |
| **Exactly-Once** | Kafka transactions | Not available | Not available |

## DLQ Approach

| Broker | DLQ mechanism |
|---|---|
| Kafka | Manual: produce failed msg to `*-dlq` topic, then commitSync |
| RabbitMQ | Native DLX: `basicNack(requeue=false)` → auto-routed by broker |
| Redis | Manual: XAUTOCLAIM stuck PEL entries → XADD to `*-dlq-stream` |
