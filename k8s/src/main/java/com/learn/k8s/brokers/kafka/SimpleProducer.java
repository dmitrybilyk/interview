package com.learn.k8s.brokers.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

public class SimpleProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            String topic = "my-manual-topic";
            String key = "id_123";
            String value = "Привіт з чистого Java коду!";

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            
            // Відправка (асинхронна)
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("Відправлено! Офсет: " + metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            });
            
            // Чекаємо трохи, щоб повідомлення встигло піти перед закриттям
            producer.flush();
        }
        System.out.println("Повідомлення успішно надіслано в Kafka!");
    }
}