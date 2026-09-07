package com.conduct.interview._13_event_driven.message_brokers.kafka.examples;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Sends keyed messages so Kafka routes same key → same partition.
 *
 * Run after _0_TopicSetup.
 * Open Kafka-UI → orders-topic to see which partition each key lands on.
 *
 * Key insight: key hashing guarantees ordering per entity (e.g. all events
 * for order-1 always go to the same partition in the same order).
 */
public class _1_KeyedProducer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, _0_TopicSetup.BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // acks=all → leader waits for all in-sync replicas before confirming
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            for (int i = 1; i <= 12; i++) {
                String key   = "order-" + (i % 3);   // 3 distinct keys → 3 partitions
                String value = "{\"orderId\":" + i + ",\"amount\":" + (i * 100) + "}";

                ProducerRecord<String, String> record =
                        new ProducerRecord<>(_0_TopicSetup.ORDERS, key, value);

                producer.send(record, (meta, ex) -> {
                    if (ex == null)
                        System.out.printf("Sent key=%-10s → partition=%d offset=%d%n",
                                key, meta.partition(), meta.offset());
                    else
                        ex.printStackTrace();
                });
            }
            producer.flush();
            System.out.println("Done.");
        }
    }
}
