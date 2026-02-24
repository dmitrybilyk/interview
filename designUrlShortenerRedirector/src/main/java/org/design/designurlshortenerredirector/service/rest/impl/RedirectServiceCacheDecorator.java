package org.design.designurlshortenerredirector.service.rest.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenerredirector.service.rest.api.RedirectService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
@Primary
//@RequiredArgsConstructor
public class RedirectServiceCacheDecorator implements RedirectService {

    private final RedirectService delegate;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final String CACHE_KEY_PREFIX = "url:";

    public RedirectServiceCacheDecorator(@Qualifier("RedirectServiceWebClientImpl") RedirectServiceWebClientImpl delegate,
                                         ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.delegate = delegate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<String> resolve(String shortCode) {
        String cacheKey = CACHE_KEY_PREFIX + shortCode;

        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .doOnError(e -> log.error("Redis error for key {}: {}", cacheKey, e.getMessage())) // Додайте це
                .doOnNext(url -> log.info("=== CACHE HIT === for code: {}", shortCode))
                .switchIfEmpty(
                        Mono.defer(() -> { // Використовуйте defer для ледачої ініціалізації
                            log.info("=== CACHE MISS === for code: {}", shortCode);
                            return delegate.resolve(shortCode)
                                    .flatMap(url -> reactiveRedisTemplate.opsForValue()
                                            .set(cacheKey, url, Duration.ofHours(1))
                                            .thenReturn(url));
                        })
                );
    }

    @Override
    public void evict(String shortCode) {
        log.info("Evicting cache decorator for: {}", shortCode);
        reactiveRedisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + shortCode).subscribe();
        delegate.evict(shortCode);
    }

    @Override
    public void recordClick(String shortCode, HttpServletRequest request) {
        delegate.recordClick(shortCode, request);
    }
}