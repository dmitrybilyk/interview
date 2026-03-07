package org.design.designurlshortenergenerator.generator.state.impl;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.design.designurlshortenergenerator.generator.state.api.UrlServiceState;
import org.design.designurlshortenergenerator.persistence.model.UrlMapping;
import org.design.designurlshortenergenerator.service.impl.UrlGeneratorServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class ActiveState implements UrlServiceState {

    @Override
    // The circuit opens after 3 consecutive failures; stays open for 5 seconds
    @CircuitBreaker(
            retryFor = {RuntimeException.class}, // Ensure it catches your simulated error
            maxAttempts = 2,                    // Trip faster
            openTimeout = 10000L,               // Errors within 10s count towards tripping
            resetTimeout = 30000L               // Stay OPEN for 30s so you have time to test
    )

    @RateLimiter(name = "shortenLimit", fallbackMethod = "rateLimitFallback")
    public String shorten(UrlGeneratorServiceImpl context, String originalUrl) {
        long id = context.getIdProvider().nextId();
        var strategy = context.getRegistry().getStrategy("base62CodeGeneratorStrategy");
        String code = strategy.encode(id);

        // Save to SQL (Primary)
        UrlMapping m = new UrlMapping();
        m.setId(id);
        m.setShortCode(code);
        m.setTarget(originalUrl);
        context.getJpaRepo().saveMapping(m);

        // Potentially failing operation (MongoDB)
        context.saveToMongo(id, code, originalUrl);

        return code;
    }

    public String rateLimitFallback(UrlGeneratorServiceImpl context, String originalUrl, Throwable t) {
        log.warn("Rate limit triggered for: {}", originalUrl);
        // Throwing an exception stops the Controller from proceeding to URI.create()
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Slow down! Too many requests.");
    }

    /**
     * The @Recover method acts as the "fallback" logic when the circuit is OPEN
     * or the retry attempts are exhausted.
     */
    @Recover
    public String recover(Exception e, UrlGeneratorServiceImpl context, String originalUrl) {
        log.error("Mongo Circuit Broken! Falling back to SQL-only. Error: {}", e.getMessage());

        long id = context.getIdProvider().nextId();
        var strategy = context.getRegistry().getStrategy("base62CodeGeneratorStrategy");
        String code = strategy.encode(id);

        // We only save to SQL here to ensure the user gets their URL
        UrlMapping m = new UrlMapping();
        m.setId(id);
        m.setShortCode(code);
        m.setTarget(originalUrl);
        context.getJpaRepo().saveMapping(m);

        return code;
    }
}