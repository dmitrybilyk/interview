package com.conduct.interview._6_message_brokers.kafka.schema_based;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;

public class AvroTopicCreator {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (AdminClient adminClient = AdminClient.create(props)) {
            String topicName = "avro-orders-topic";
            int partitions = 2;
            short replicationFactor = 1; // Use 3 in production environments

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();

            System.out.println("✅ Avro Topic '" + topicName + "' successfully created.");
        } catch (Exception e) {
            System.err.println("❌ Topic initialization failed: " + e.getMessage());
        }
    }
}