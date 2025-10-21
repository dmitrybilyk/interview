package com.conduct.interview.messaging.kafka.person;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

public class PersonProducer {

    public static void main(String[] args) {
        String topic = "person-topic";
        sendPeople(topic);
    }

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
                                p, metadata.topic(), metadata.partition(), metadata.offset()
                        );
                    } else {
                        exception.printStackTrace();
                    }
                });
            }
            producer.flush();
        }

        System.out.println("\n🚀 Producer finished sending messages.\n");
    }
}
