package com.conduct.interview._13_event_driven.message_brokers.kafka.examples;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * AT-LEAST-ONCE delivery strategy.
 *
 * How it works:
 *   Commit offset AFTER processing is complete.
 *   If the app crashes during processing → on restart the same message is redelivered.
 *
 * Trade-off:
 *   + No message loss
 *   – Possible duplicates (consumer must be idempotent)
 *
 * When to use:
 *   Most business use-cases: orders, payments, notifications.
 *   Pair with idempotent processing (dedup by messageId, upsert, etc.)
 *
 * To try:
 *   1. Run _1_KeyedProducer.
 *   2. Run this consumer and kill it mid-run (Ctrl+C).
 *   3. Restart → watch messages reprocessed from last committed offset.
 */
public class _3_AtLeastOnceConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  _0_TopicSetup.BOOTSTRAP);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,           "at-least-once-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,  "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(_0_TopicSetup.ORDERS));

            System.out.println("[AT-LEAST-ONCE] Started. Commit happens AFTER processing.");

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> r : records) {
                    System.out.printf("[AT-LEAST-ONCE] partition=%d offset=%d key=%s  processing...%n",
                            r.partition(), r.offset(), r.key());
                    process(r.value());
                    // Commit only after successful processing
                    consumer.commitSync();
                    System.out.println("  committed.");
                }
            }
        }
    }

    static void process(String value) {
        System.out.println("  processed: " + value);
    }
}
