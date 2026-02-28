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
            // Створюємо топік з 2 партіціями
            // Якщо топік вже є, його треба видалити через UI або змінити назву тут
            String topicName = "my-manual-topic";
            int partitions = 2;
            short replicationFactor = 1;

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();

            System.out.println("✅ Топік '" + topicName + "' створено з " + partitions + " партіціями!");
        } catch (Exception e) {
            System.err.println("❌ Помилка: можливо, топік уже існує. Видали його через Kafka UI.");
            e.printStackTrace();
        }
    }
}