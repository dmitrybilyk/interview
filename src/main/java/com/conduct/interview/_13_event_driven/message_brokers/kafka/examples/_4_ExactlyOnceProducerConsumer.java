package com.conduct.interview._13_event_driven.message_brokers.kafka.examples;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.*;

/**
 * EXACTLY-ONCE delivery strategy (read-process-write within one transaction).
 *
 * How it works:
 *   Producer uses idempotence + transactions.
 *   Consumer uses isolation.level=read_committed to skip uncommitted data.
 *   Offset is committed inside the Kafka transaction — atomically with the output write.
 *   If the process crashes mid-transaction → transaction is aborted, nothing is written,
 *   and on restart the same input is reprocessed cleanly.
 *
 * Trade-off:
 *   + Exactly-once semantics end-to-end (within Kafka)
 *   – Lower throughput, ~2-3x overhead vs at-least-once
 *   – Only works for Kafka-to-Kafka pipelines (not for DB writes)
 *
 * When to use:
 *   Financial aggregations, stream processing where duplicates are unacceptable
 *   and the output is another Kafka topic.
 *
 * To try:
 *   1. Run _1_KeyedProducer first.
 *   2. Run this class — it reads from orders-topic, transforms, writes to tx-topic.
 *   3. Check tx-topic in Kafka-UI: only committed records are visible.
 */
public class _4_ExactlyOnceProducerConsumer {

    public static void main(String[] args) throws Exception {
        KafkaConsumer<String, String> consumer = buildConsumer();
        KafkaProducer<String, String> producer = buildTransactionalProducer();

        producer.initTransactions();
        consumer.subscribe(List.of(_0_TopicSetup.ORDERS));

        System.out.println("[EXACTLY-ONCE] Started. Reading orders-topic → writing tx-topic.");

        for (int i = 0; i < 3; i++) {   // 3 poll rounds for the demo
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
            if (records.isEmpty()) continue;

            producer.beginTransaction();
            try {
                for (ConsumerRecord<String, String> r : records) {
                    String enriched = r.value().replace("}", ",\"processed\":true}");
                    producer.send(new ProducerRecord<>(_0_TopicSetup.TX_TOPIC, r.key(), enriched));
                    System.out.printf("[EXACTLY-ONCE] forwarded key=%s partition=%d offset=%d%n",
                            r.key(), r.partition(), r.offset());
                }
                // Commit consumer offsets inside the transaction
                Map<org.apache.kafka.common.TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                for (ConsumerRecord<String, String> r : records) {
                    offsets.put(
                            new org.apache.kafka.common.TopicPartition(r.topic(), r.partition()),
                            new OffsetAndMetadata(r.offset() + 1)
                    );
                }
                producer.sendOffsetsToTransaction(offsets, consumer.groupMetadata());
                producer.commitTransaction();
                System.out.println("  transaction committed.");
            } catch (Exception e) {
                producer.abortTransaction();
                System.out.println("  transaction ABORTED: " + e.getMessage());
            }
        }

        consumer.close();
        producer.close();
    }

    static KafkaConsumer<String, String> buildConsumer() {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, _0_TopicSetup.BOOTSTRAP);
        p.put(ConsumerConfig.GROUP_ID_CONFIG,           "exactly-once-group");
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,  "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // Only see messages from committed transactions
        p.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return new KafkaConsumer<>(p);
    }

    static KafkaProducer<String, String> buildTransactionalProducer() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, _0_TopicSetup.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,    "true");
        p.put(ProducerConfig.ACKS_CONFIG,                  "all");
        // Unique transactional ID per producer instance
        p.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,      "orders-processor-1");
        return new KafkaProducer<>(p);
    }
}
