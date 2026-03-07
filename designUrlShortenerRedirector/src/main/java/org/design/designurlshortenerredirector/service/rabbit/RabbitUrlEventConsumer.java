package org.design.designurlshortenerredirector.service.rabbit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitUrlEventConsumer {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CACHE_KEY_PREFIX = "url:";

    @RabbitListener(queues = "url.shorten.queue")
    public void consume(String message) {
        try {
            log.info("=== RABBITMQ RECEIVED === {}", message);
            JsonNode jsonNode = objectMapper.readTree(message);
            String shortCode = jsonNode.get("shortCode").asText();
            String longUrl = jsonNode.get("longUrl").asText();

            String cacheKey = CACHE_KEY_PREFIX + shortCode;

            // Reactive update to Redis
            redisTemplate.opsForValue()
                    .set(cacheKey, longUrl, Duration.ofHours(24))
                    .doOnSuccess(success -> log.info("=== RABBIT CACHE UPDATE === Key: {}, Success: {}", cacheKey, success))
                    .subscribe();

        } catch (Exception e) {
            log.error("Error processing RabbitMQ message: {}", message, e);
        }
    }
}