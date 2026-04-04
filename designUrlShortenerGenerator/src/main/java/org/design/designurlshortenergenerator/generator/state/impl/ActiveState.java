package org.design.designurlshortenergenerator.generator.state.impl;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.instrument.MeterRegistry;
import org.design.designurlshortenergenerator.exceptions.SomeBusinessException;
import org.design.designurlshortenergenerator.generator.state.api.UrlServiceState;
import org.design.designurlshortenergenerator.model.api.ShortCode;
import org.design.designurlshortenergenerator.model.impl.Base62ShortCode;
import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;
import org.design.designurlshortenergenerator.model.visitor.impl.LoggingVisitor;
import org.design.designurlshortenergenerator.model.visitor.impl.MetricsVisitor;
import org.design.designurlshortenergenerator.model.visitor.impl.ValidationVisitor;
import org.design.designurlshortenergenerator.persistence.model.sql.UrlMapping;
import org.design.designurlshortenergenerator.service.audit.AuditService;
import org.design.designurlshortenergenerator.service.events.EventService;
import org.design.designurlshortenergenerator.service.generator.impl.UrlGeneratorServiceImpl;
import org.design.designurlshortenergenerator.service.messaging.api.UrlEventPublisher;
import org.design.designurlshortenergenerator.service.notification.EmailProvider;
import org.design.designurlshortenergenerator.service.storage.api.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Slf4j
@Component
public class ActiveState implements UrlServiceState {

    private final StorageService storageService;
    private final UrlEventPublisher urlEventPublisher;
    private final EmailProvider emailProvider;
    private final AuditService auditService;
    private final EventService eventService;

    // This is the "shared state"
    // Using a simple long (NOT AtomicLong) to intentionally cause race conditions
    private AtomicLong totalRequestsProcessed = new AtomicLong();
//    private long totalRequestsProcessed = 0;

    // For Grafana/Prometheus visibility
    private final MeterRegistry meterRegistry;

    public ActiveState(StorageService storageService, UrlEventPublisher urlEventPublisher, EmailProvider emailProvider, AuditService auditService, EventService eventService,
                       MeterRegistry meterRegistry) {
        this.storageService = storageService;
        this.urlEventPublisher = urlEventPublisher;
        this.emailProvider = emailProvider;
        this.auditService = auditService;
        this.eventService = eventService;
        this.meterRegistry = meterRegistry;
        io.micrometer.core.instrument.Gauge.builder("url.shorten.shared.counter", () -> totalRequestsProcessed.get())
//        io.micrometer.core.instrument.Gauge.builder("url.shorten.shared.counter", () -> totalRequestsProcessed)
                .description("A non-thread-safe counter to demonstrate race conditions")
                .register(meterRegistry);
    }

    @Override
    // The circuit opens after 3 consecutive failures; stays open for 5 seconds
//    @CircuitBreaker(
//            retryFor = {RuntimeException.class}, // Ensure it catches your simulated error
//            maxAttempts = 2,                    // Trip faster
//            openTimeout = 10000L,               // Errors within 10s count towards tripping
//            resetTimeout = 30000L               // Stay OPEN for 30s so you have time to test
//    )
//    @Transactional(noRollbackFor = NullPointerException.class)
    @Transactional
//    @RateLimiter(name = "shortenLimit", fallbackMethod = "rateLimitFallback")
    public String shorten(UrlGeneratorServiceImpl context, String originalUrl) {
        long id = context.getIdProvider().nextId();
        var strategy = context.getRegistry().getStrategy("base62CodeGeneratorStrategy");

        ShortCodeVisitor validator = new ValidationVisitor();
        ShortCodeVisitor metrics = new MetricsVisitor(meterRegistry);
        ShortCodeVisitor logging = new LoggingVisitor();

        ShortCode code = new Base62ShortCode(strategy.encode(id));

// visitors
        code.accept(validator);
        code.accept(metrics);
        code.accept(logging);

        totalRequestsProcessed.incrementAndGet();

// 1. persist FIRST
        storageService.save(id, code.getValue(), originalUrl);

//        String s = null;
//        s.equals("");

//        try {
//            auditService.saveAudit("CREATED: " + code);
//            eventService.saveEvent("Saved Event");
//            methodToFail();
//        } catch (Exception e) {
//             swallow or log
//        }

// 2. only then external side effects
        urlEventPublisher.publishUrlCreated(code.getValue(), originalUrl);
        emailProvider.send("recipient", "message body");

        return code.getValue();
    }

    private void methodToFail() {
        throw new SomeBusinessException("FAIL");
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


    @Transactional(isolation = SERIALIZABLE)
//    @Transactional(isolation = READ_COMMITTED)
    public void incrementClicks(String code) {
        // In READ_COMMITTED, two threads can read the same value (e.g., 10)
        UrlMapping mapping = storageService.findByCode(code);

        if (mapping != null) {
            log.info("Thread {} read count: {}", Thread.currentThread().getName(), mapping.getClickCount());

            // Simulating processing time to increase race condition probability
            try { Thread.sleep(50); } catch (InterruptedException e) { }

            mapping.setClickCount(mapping.getClickCount() + 1);
            storageService.saveMapping(mapping);
        }
    }

}