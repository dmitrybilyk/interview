package com.conduct.interview._13_event_driven.message_brokers.kafka.examples;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CONSUMER GROUP SCALING demo.
 *
 * Starts 3 consumers in the same group against orders-topic (3 partitions).
 * Kafka rebalances so each consumer owns exactly one partition.
 *
 * Key observations:
 *   - Consumer A, B, C each print which partition they process.
 *   - If you kill one consumer → Kafka rebalances within ~10s, remaining two
 *     share its partition.
 *   - Adding a 4th consumer leaves one idle (can't have more consumers than partitions).
 *
 * To try:
 *   1. Run _1_KeyedProducer (produces to 3-partition topic with keys 0/1/2).
 *   2. Run this class → see partition ownership logged.
 *   3. Comment out one thread (or reduce CONSUMERS) → see rebalance.
 */
public class _6_ConsumerGroupScaling {

    static final int CONSUMERS = 3;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(CONSUMERS);

        for (int i = 0; i < CONSUMERS; i++) {
            final String name = "consumer-" + i;
            pool.submit(() -> runConsumer(name));
        }

        Thread.sleep(20_000);  // run demo for 20 seconds
        pool.shutdownNow();
    }

    static void runConsumer(String name) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  _0_TopicSetup.BOOTSTRAP);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,           "scaling-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,  "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(
                    List.of(_0_TopicSetup.ORDERS),
                    new ConsumerRebalanceListener() {
                        public void onPartitionsRevoked(java.util.Collection<org.apache.kafka.common.TopicPartition> p) {
                            System.out.printf("[%s] REVOKED partitions: %s%n", name, p);
                        }
                        public void onPartitionsAssigned(java.util.Collection<org.apache.kafka.common.TopicPartition> p) {
                            System.out.printf("[%s] ASSIGNED partitions: %s%n", name, p);
                        }
                    }
            );

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
                for (ConsumerRecord<String, String> r : records) {
                    System.out.printf("[%s] partition=%d offset=%d key=%s%n",
                            name, r.partition(), r.offset(), r.key());
                    consumer.commitSync();
                }
            }
        } catch (Exception ignored) {}
    }
}
