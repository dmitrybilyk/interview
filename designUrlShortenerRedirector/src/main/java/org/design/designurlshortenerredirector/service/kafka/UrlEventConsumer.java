package org.design.designurlshortenerredirector.service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlEventConsumer {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // Використовуємо ту саму константу, що і в декораторі, щоб уникнути помилок
    private static final String CACHE_KEY_PREFIX = "url:";

    @KafkaListener(topics = "url-created", groupId = "redirector-group")
    public void consume(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String shortCode = jsonNode.get("shortCode").asText();
            String longUrl = jsonNode.get("longUrl").asText();

            String cacheKey = CACHE_KEY_PREFIX + shortCode;

            // Оновлюємо кеш Redis асинхронно
            redisTemplate.opsForValue()
                    .set(cacheKey, longUrl, Duration.ofHours(24))
                    .subscribe(success -> log.info("=== KAFKA CACHE UPDATE === Key: {}, Success: {}", cacheKey, success));

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }
}