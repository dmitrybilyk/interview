package com.conduct.interview._6_message_brokers.kafka.schema_based;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class AvroProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Key is a simple String, Value is a structured Avro GenericRecord
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        
        // CRITICAL: Point the client to the Confluent Schema Registry URL
        props.put("schema.registry.url", "http://localhost:8081");

        try (KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(props)) {
            String topic = "avro-orders-topic";

            for (int i = 1; i <= 5; i++) {
                String userId = "user_" + i;
                String orderId = "ORD-100" + i;
                
                // Build our complex object conforming to the schema
                GenericRecord orderPayload = OrderModelFactory.createOrderRecord(
                        orderId, userId, 99.99 * i, "PENDING"
                );

                ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topic, userId, orderPayload);

                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.printf("🚀 Dispatched Avro Event -> Partition: %d | Offset: %d | Key: %s%n",
                                metadata.partition(), metadata.offset(), userId);
                    } else {
                        exception.printStackTrace();
                    }
                });
            }
            producer.flush();
        }
        System.out.println("🏁 Production batch complete.");
    }
}