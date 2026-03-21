package org.design.designurlshortenergenerator.service.messaging.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.design.designurlshortenergenerator.service.messaging.api.UrlEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.type", havingValue = "kafka")
public class KafkaUrlEventPublisher implements UrlEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.visits:url-created}")
    private String topicName;
    @Value("${app.kafka.topic.visits:url-deleted}")
    private String deletionTopicName;

    @Override
    @SneakyThrows
    public void publishUrlCreated(String shortCode, String longUrl) {
        String payload = objectMapper.writeValueAsString(Map.of(
            "shortCode", shortCode,
            "longUrl", longUrl
        ));
        kafkaTemplate.send(topicName, shortCode, payload);
    }

    @Override
    @SneakyThrows
    public void publishUrlDeleted(String code) {
        String payload = objectMapper.writeValueAsString(Map.of(
                "shortCode", code
        ));
        kafkaTemplate.send(deletionTopicName, code, payload);
    }
}