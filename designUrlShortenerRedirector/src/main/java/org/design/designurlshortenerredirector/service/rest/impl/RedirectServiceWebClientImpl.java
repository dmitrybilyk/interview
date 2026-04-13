package org.design.designurlshortenerredirector.service.rest.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenerredirector.email.api.EmailService;
import org.design.designurlshortenerredirector.exception.NotFoundException;
import org.design.designurlshortenerredirector.service.rest.api.RedirectService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service("RedirectServiceWebClientImpl")
@RequiredArgsConstructor
@Slf4j
//@Primary
public class RedirectServiceWebClientImpl implements RedirectService {

    private final WebClient.Builder webClientBuilder;
    private final MeterRegistry meterRegistry;
    private final EmailService emailService;

    private static final String CACHE_NAME = "url-cache";
    // У реальному житті це має бути в application.yml
    private final String codeServiceUrl = "http://localhost:8082/api/codes/"; 

    @Override
    public Mono<String> resolve(String shortCode) {
        log.info("Resolving shortCode via WebClient: {}", shortCode);

        return webClientBuilder.build()
                .get()
                .uri(codeServiceUrl + "{code}", shortCode)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), 
                    response -> Mono.error(new NotFoundException("Short code not found in CodeService: " + shortCode)))
                .bodyToMono(String.class) // Припустимо, CodeService повертає просто String URL
                .flatMap(originalUrl -> {
                    // Асинхронне сповіщення поштою (як було раніше)
                    emailService.sendNotificationEmail(shortCode, originalUrl)
                            .doOnError(e -> log.error("Async email failed", e))
                            .onErrorResume(e -> Mono.empty())
                            .subscribe();
                    
                    return Mono.just(originalUrl);
                });
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
                .tag("provider", "webclient")
                .register(meterRegistry)
                .increment();
    }
}