package org.design.designurlshortenerredirector;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectService {

    private final UrlMappingRepository pgRepo;
    private final RedisTemplate<String, String> redisTemplate;
    private final MeterRegistry meterRegistry;

    private static final String CACHE_NAME = "url-cache";

    @Cacheable(value = CACHE_NAME, key = "#shortCode")
    public String resolve(String shortCode) {
        log.info("Cache MISS for shortCode: {}", shortCode);
        return pgRepo.findByShortCode(shortCode)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new NotFoundException("Short code not found: " + shortCode));
    }

    // Optional: manual cache eviction (e.g., on new URL creation)
    @CacheEvict(value = CACHE_NAME, key = "#shortCode")
    public void evict(String shortCode) {
        log.info("Evicted cache for: {}", shortCode);
    }

    // Optional: record click
    public void recordClick(String shortCode, HttpServletRequest request) {
        Counter.builder("redirect.requests")
                .tag("code", shortCode)
                .tag("cached", "false") // will be overridden if cached
                .register(meterRegistry)
                .increment();
    }
}