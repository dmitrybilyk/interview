package org.design.designurlshortenergenerator.service.messaging.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.service.messaging.api.UrlEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.type", havingValue = "rabbitmq")
public class RabbitUrlEventPublisher implements UrlEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    // These should match the names defined in the Redirector's config
    private static final String EXCHANGE_NAME = "url.exchange";
    private static final String ROUTING_KEY = "url.created";

    @Override
    @SneakyThrows
    public void publishUrlCreated(String shortCode, String longUrl) {
        String payload = objectMapper.writeValueAsString(Map.of(
            "shortCode", shortCode,
            "longUrl", longUrl
        ));

        // RabbitMQ uses an Exchange and a Routing Key to decide where the message goes
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, payload);
        
        log.info("Sent event to RabbitMQ: {}", shortCode);
    }
}