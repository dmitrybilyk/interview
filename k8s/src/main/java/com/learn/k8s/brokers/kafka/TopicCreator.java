package com.learn.k8s.brokers.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import java.util.Collections;
import java.util.Properties;

public class TopicCreator {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (AdminClient adminClient = AdminClient.create(props)) {
            NewTopic newTopic = new NewTopic("my-manual-topic", 1, (short) 1);
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            System.out.println("Топік створено успішно! 🎉");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}