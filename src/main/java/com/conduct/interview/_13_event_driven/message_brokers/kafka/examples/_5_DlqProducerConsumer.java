package com.conduct.interview._13_event_driven.message_brokers.kafka.examples;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * DEAD-LETTER QUEUE (DLQ) pattern for Kafka.
 *
 * Kafka has no native DLQ — we implement it manually:
 *   1. Consumer reads from orders-topic (at-least-once).
 *   2. If processing fails → publish the poison message to orders-dlq.
 *   3. Commit offset normally so the main topic moves on.
 *   4. A separate DLQ consumer (bottom of this file) monitors orders-dlq for alerting/reprocessing.
 *
 * To try:
 *   1. Run _1_KeyedProducer (sends 12 messages).
 *   2. Run this class.
 *   3. Every message whose orderId % 4 == 0 is treated as "bad" → goes to DLQ.
 *   4. Open Kafka-UI → check orders-dlq topic.
 */
public class _5_DlqProducerConsumer {

    public static void main(String[] args) throws InterruptedException {
        // Start DLQ monitor in background thread
        Thread dlqMonitor = new Thread(_5_DlqProducerConsumer::runDlqConsumer, "dlq-monitor");
        dlqMonitor.setDaemon(true);
        dlqMonitor.start();

        runMainConsumer();
    }

    // ── Main consumer ──────────────────────────────────────────────────────────

    static void runMainConsumer() {
        Properties cp = consumerProps("dlq-demo-group");
        Properties pp = producerProps();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(cp);
             KafkaProducer<String, String> dlqProducer = new KafkaProducer<>(pp)) {

            consumer.subscribe(List.of(_0_TopicSetup.ORDERS));
            System.out.println("[MAIN] Started. Bad messages → " + _0_TopicSetup.DLQ);

            long deadline = System.currentTimeMillis() + 15_000;
            while (System.currentTimeMillis() < deadline) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
                for (ConsumerRecord<String, String> r : records) {
                    try {
                        processOrThrow(r);
                        consumer.commitSync();
                        System.out.printf("[MAIN] OK   partition=%d offset=%d key=%s%n",
                                r.partition(), r.offset(), r.key());
                    } catch (Exception e) {
                        // Publish to DLQ with original metadata in headers
                        ProducerRecord<String, String> dead =
                                new ProducerRecord<>(_0_TopicSetup.DLQ, r.key(), r.value());
                        dead.headers().add("original-topic", _0_TopicSetup.ORDERS.getBytes());
                        dead.headers().add("error",          e.getMessage().getBytes());
                        dlqProducer.send(dead);
                        // Still commit — so we move past the poison message
                        consumer.commitSync();
                        System.out.printf("[MAIN] FAIL → DLQ  key=%s  error=%s%n", r.key(), e.getMessage());
                    }
                }
            }
        }
    }

    static void processOrThrow(ConsumerRecord<String, String> r) {
        // Simulate: messages containing "orderId":4, 8, 12 fail
        if (r.value().contains("\"orderId\":4")
                || r.value().contains("\"orderId\":8")
                || r.value().contains("\"orderId\":12")) {
            throw new RuntimeException("Invalid order amount");
        }
    }

    // ── DLQ consumer (monitors dead letters) ──────────────────────────────────

    static void runDlqConsumer() {
        Properties cp = consumerProps("dlq-monitor-group");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(cp)) {
            consumer.subscribe(List.of(_0_TopicSetup.DLQ));
            System.out.println("[DLQ] Monitor started → watching " + _0_TopicSetup.DLQ);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> r : records) {
                    String error = r.headers().lastHeader("error") != null
                            ? new String(r.headers().lastHeader("error").value()) : "unknown";
                    System.out.printf("[DLQ] DEAD LETTER key=%s  error='%s'  value=%s%n",
                            r.key(), error, r.value());
                    consumer.commitSync();
                }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    static Properties consumerProps(String groupId) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, _0_TopicSetup.BOOTSTRAP);
        p.put(ConsumerConfig.GROUP_ID_CONFIG,          groupId);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");
        return p;
    }

    static Properties producerProps() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, _0_TopicSetup.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.ACKS_CONFIG, "all");
        return p;
    }
}
