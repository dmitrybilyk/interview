package com.conduct.interview._13_event_driven.message_brokers.kafka.examples;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * AT-MOST-ONCE delivery strategy.
 *
 * How it works:
 *   Commit offset BEFORE processing the message.
 *   If the app crashes during processing → message is lost (never reprocessed).
 *
 * Trade-off:
 *   + No duplicate processing
 *   – Possible message loss on crash
 *
 * When to use:
 *   Metrics / analytics where occasional loss is acceptable.
 *
 * To try:
 *   1. Run _1_KeyedProducer.
 *   2. Run this consumer.
 *   3. Add Thread.sleep + kill it mid-run → see which messages were skipped.
 */
public class _2_AtMostOnceConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  _0_TopicSetup.BOOTSTRAP);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,           "at-most-once-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,  "earliest");
        // disable auto-commit so we control when offset is committed
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(_0_TopicSetup.ORDERS));

            System.out.println("[AT-MOST-ONCE] Started. Commit happens BEFORE processing.");

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> r : records) {
                    // Commit offset first — if we crash below, message is LOST
                    consumer.commitSync();
                    System.out.printf("[AT-MOST-ONCE] partition=%d offset=%d key=%s  processing...%n",
                            r.partition(), r.offset(), r.key());
                    process(r.value());
                }
            }
        }
    }

    static void process(String value) {
        // Simulate work
        System.out.println("  processed: " + value);
    }
}
