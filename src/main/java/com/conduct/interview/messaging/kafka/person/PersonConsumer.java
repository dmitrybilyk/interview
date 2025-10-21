package com.conduct.interview.messaging.kafka.person;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.*;

public class PersonConsumer {

    public static void main(String[] args) {
        String topic = "person-topic";
        consumePeople(topic, "person-group-1");
    }

    public static void consumePeople(String topic, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PersonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, Person> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            System.out.printf("👂 Consumer %s listening on topic %s...%n", groupId, topic);

            while (true) {
                ConsumerRecords<String, Person> records = consumer.poll(Duration.ofSeconds(2));
                for (ConsumerRecord<String, Person> record : records) {
                    System.out.printf(
                            "Received 📥 [%s] key=%s value=%s partition=%d offset=%d%n",
                            groupId, record.key(), record.value(),
                            record.partition(), record.offset()
                    );
                }
            }
        }
    }
}
