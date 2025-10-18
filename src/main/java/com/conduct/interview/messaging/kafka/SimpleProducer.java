package com.conduct.interview.messaging.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SimpleProducer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1️⃣ Producer configuration
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");        // wait for all replicas
        props.put(ProducerConfig.RETRIES_CONFIG, 3);         // retry on failure
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);       // small delay to batch
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "simple-producer-demo");

        // 2️⃣ Create KafkaProducer
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            // 3️⃣ Create a record
            String topic = "my-new-topic";
            String key = "key1";
            String value = "Hello from Java plain producer!7779";

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            Future<RecordMetadata> send = producer.send(record);
            System.out.println(send.get().offset());

            producer.send(record, (recordMetadata, e) -> {
                System.out.println(recordMetadata.partition());
            });

            // 4️⃣ Send (async)
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf("✅ Sent to topic=%s partition=%d offset=%d%n",
                            metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            });

            // 5️⃣ Flush and close
            producer.flush();
        }
    }
}
