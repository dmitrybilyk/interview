package org.design.designurlshortenergenerator.service.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.service.messaging.api.UrlEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheInvalidationListener {
    private final com.github.benmanes.caffeine.cache.Cache<String, String> caffeineCache;
    private final UrlEventPublisher kafkaPublisher;

    @EventListener
    public void handleCacheCleanup(DeleteUrlCommand command) {
        log.info("Command Received: Evicting {} from Caches", command.code());
        
        // 1. Local Cache
        caffeineCache.invalidate(command.code());
        
        // 2. Remote Cache (Redirector) via Kafka
        kafkaPublisher.publishUrlDeleted(command.code()); 
    }
}