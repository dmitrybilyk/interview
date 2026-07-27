package com.conduct.interview._6_message_brokers.kafka.schema_based;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class AvroConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro-order-processing-group");
        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        
        // Connect to Schema Registry to pull down appropriate decoding metadata
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("avro-orders-topic"));

            System.out.println("📡 Avro Consumer engine active, polling stream...");

            while (true) {
                ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, GenericRecord> record : records) {
                    GenericRecord avroPayload = record.value();
                    
                    // Extract fields dynamically using string identifiers
                    String orderId = avroPayload.get("orderId").toString();
                    String userId = avroPayload.get("userId").toString();
                    Double amount = (Double) avroPayload.get("amount");
                    String status = avroPayload.get("status").toString();

                    System.out.printf("📥 CONSUMED -> Key: %s | Partition: %d | Offset: %d%n" +
                                    "   📦 Object Data -> OrderID: %s, User: %s, Total: $%.2f, Status: %s%n",
                            record.key(), record.partition(), record.offset(), orderId, userId, amount, status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}