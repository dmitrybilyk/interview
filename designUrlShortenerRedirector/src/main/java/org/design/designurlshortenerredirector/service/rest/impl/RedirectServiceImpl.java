package org.design.designurlshortenerredirector.service.rest.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenerredirector.persistence.model.UrlMapping;
import org.design.designurlshortenerredirector.email.api.EmailService;
import org.design.designurlshortenerredirector.exception.NotFoundException;
import org.design.designurlshortenerredirector.persistence.UrlMappingRepository;
import org.design.designurlshortenerredirector.service.rest.api.RedirectService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
//@Primary
public class RedirectServiceImpl implements RedirectService {

    private final UrlMappingRepository pgRepo;
    private final RedisTemplate<String, String> redisTemplate;
    private final MeterRegistry meterRegistry;
    private final EmailService emailService;

    private static final String CACHE_NAME = "url-cache";

//    @Cacheable(value = CACHE_NAME, key = "#shortCode")
//    public String resolve(String shortCode) {
//        log.info("Cache MISS for shortCode: {}", shortCode);
//        return pgRepo.findByShortCode(shortCode)
//                .map(UrlMapping::getOriginalUrl)
//                .orElseThrow(() -> new NotFoundException("Short code not found: " + shortCode));
//    }

    @Override
    public Mono<String> resolve(String shortCode) {
        log.info("Resolving shortCode: {}", shortCode);

        return pgRepo.findByShortCode(shortCode)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(urlMapping -> {
                    String originalUrl = urlMapping.getOriginalUrl();

                    Mono<Void> emailMono = emailService.sendNotificationEmail(shortCode, originalUrl)
                            .doOnError(e -> log.error("Async email failed for shortCode: {}", shortCode, e))
                            .onErrorResume(e -> Mono.empty());

                    emailMono.subscribe();

                    log.info("Email notification launched asynchronously for shortCode: {}", shortCode);
                })
                .map(UrlMapping::getOriginalUrl)
                .switchIfEmpty(Mono.error(new NotFoundException("Short code not found: " + shortCode)));
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#shortCode")
    public void evict(String shortCode) {
        log.info("Evicted cache for: {}", shortCode);
    }

    @Override
    public void recordClick(String shortCode, HttpServletRequest request) {
        Counter.builder("redirect.requests")
                .tag("code", shortCode)
                .tag("cached", "false")
                .register(meterRegistry)
                .increment();
    }
}