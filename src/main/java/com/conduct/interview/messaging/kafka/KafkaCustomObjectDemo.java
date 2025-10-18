package com.conduct.interview.messaging.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Single-file demo:
 *  - Sends Person objects using a custom serializer
 *  - Then runs a consumer that continuously reads them
 *
 * Requires:
 *   org.apache.kafka:kafka-clients
 *   com.fasterxml.jackson.core:jackson-databind
 */
public class KafkaCustomObjectDemo {

    // -----------------------------
    // 1️⃣ Model
    // -----------------------------
    public static class Person {
        public String name;
        public int age;

        public Person() {} // Required for Jackson

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + '}';
        }
    }

    // -----------------------------
    // 2️⃣ Serializer
    // -----------------------------
    public static class PersonSerializer implements Serializer<Person> {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, Person data) {
            try {
                if (data == null) return null;
                return mapper.writeValueAsString(data)
                        .getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("Error serializing Person", e);
            }
        }
    }

    // -----------------------------
    // 3️⃣ Deserializer
    // -----------------------------
    public static class PersonDeserializer implements Deserializer<Person> {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public Person deserialize(String topic, byte[] data) {
            try {
                if (data == null) return null;
                return mapper.readValue(
                        new String(data, StandardCharsets.UTF_8),
                        Person.class
                );
            } catch (Exception e) {
                throw new RuntimeException("Error deserializing Person", e);
            }
        }
    }

    // -----------------------------
    // 4️⃣ Main
    // -----------------------------
    public static void main(String[] args) {
        String topic = "person-topic";
        sendPeople(topic);
        consumePeople(topic);
    }

    // -----------------------------
    // 5️⃣ Producer
    // -----------------------------
    private static void sendPeople(String topic) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PersonSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, Person> producer = new KafkaProducer<>(props)) {
            for (int i = 1; i <= 5; i++) {
                Person p = new Person("User" + i, 20 + i);
                ProducerRecord<String, Person> record =
                        new ProducerRecord<>(topic, "key-" + i, p);
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.printf(
                                "✅ Sent %s to %s-%d@%d%n",
                                p, metadata.topic(), metadata.partition(),
                                metadata.offset()
                        );
                    } else {
                        exception.printStackTrace();
                    }
                });
            }
            producer.flush();
        }

        System.out.println("\n🚀 Producer done sending messages.\n");
    }

    // -----------------------------
    // 6️⃣ Consumer
    // -----------------------------
    private static void consumePeople(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "person-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                PersonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, Person> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            System.out.println("👂 Waiting for messages... Press Ctrl+C to stop.");

            while (true) {
                ConsumerRecords<String, Person> records = consumer.poll(Duration.ofSeconds(2));
                for (ConsumerRecord<String, Person> record : records) {
                    System.out.printf(
                            "📥 Received key=%s, value=%s, partition=%d, offset=%d%n",
                            record.key(), record.value(),
                            record.partition(), record.offset()
                    );
                }
            }
        }
    }
}
