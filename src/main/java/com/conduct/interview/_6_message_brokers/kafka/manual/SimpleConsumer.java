package com.conduct.interview._6_message_brokers.kafka.manual;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SimpleConsumer {
    public static void main(String[] args) {
        // 1️⃣ Consumer configuration
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // group.id identifies the consumer group (required)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");

        // read from the beginning if no offset is stored for this group
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // enable auto commit (simpler for demo)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        // 2️⃣ Create consumer
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // 3️⃣ Subscribe to the same topic as producer
            consumer.subscribe(Collections.singletonList("my-new-topic"));
            System.out.println("🟢 Listening on topic 'my-new-topic'...");

            // 4️⃣ Continuous poll loop
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf(
                        "📥 Received message | topic=%s | partition=%d | offset=%d | key=%s | value=%s%n",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value()
                    );
                }
            }
        }
    }
}
