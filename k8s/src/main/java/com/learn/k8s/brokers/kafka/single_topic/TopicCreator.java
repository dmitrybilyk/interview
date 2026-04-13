package com.learn.k8s.brokers.kafka.single_topic;

import org.apache.kafka.clients.admin.*;
import java.util.Collections;
import java.util.Properties;

public class TopicCreator {

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (AdminClient admin = AdminClient.create(props)) {

            String topicName = "events-topic";
            int partitions = 3;
            short replicationFactor = 1;

            NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);

            admin.createTopics(Collections.singleton(topic)).all().get();

            System.out.println("✅ Topic created: " + topicName);

        } catch (Exception e) {
            System.out.println("Topic probably already exists.");
        }
    }
}