package org.design.designurlshortenergenerator.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UrlEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void sendUrlCreatedEvent(String shortCode, String longUrl) {
        String payload = objectMapper.writeValueAsString(Map.of(
            "shortCode", shortCode,
            "longUrl", longUrl
        ));
        
        // Використовуємо shortCode як ключ, щоб події для одного коду завжди йшли в одну партицію
        kafkaTemplate.send("url-created", shortCode, payload);
    }
}