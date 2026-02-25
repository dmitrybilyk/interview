package org.design.designurlshortenergenerator.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.visits:url-created}")
    private String topicName;

    @Bean
    public NewTopic urlVisitsTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)    // Розділяємо на 3 частини для паралельної обробки
                .replicas(1)      // Оскільки у нас один брокер
                .build();
    }
}