package org.design.designurlshortenergenerator.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.generator.allocator.api.IdProvider;
import org.design.designurlshortenergenerator.generator.state.api.UrlServiceState;
import org.design.designurlshortenergenerator.generator.state.impl.ActiveState;
import org.design.designurlshortenergenerator.generator.state.impl.MaintenanceState;
import org.design.designurlshortenergenerator.generator.strategy.impl.CodeGeneratorRegistry;
import org.design.designurlshortenergenerator.persistence.model.UrlMapping;
import org.design.designurlshortenergenerator.persistence.mongo.model.MongoUrlMapping;
import org.design.designurlshortenergenerator.persistence.mongo.model.repository.MongoUrlMappingRepository;
import org.design.designurlshortenergenerator.persistence.repository.UrlMappingRepository;
import org.design.designurlshortenergenerator.service.api.UrlGeneratorService;
import org.design.designurlshortenergenerator.service.messaging.api.UrlEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter // Allows States to access the repos and providers
public class UrlGeneratorServiceImpl implements UrlGeneratorService {

    private final UrlMappingRepository jpaRepo;
    private final MongoUrlMappingRepository mongoRepo;
    private final IdProvider idProvider;
    private final UrlEventPublisher urlEventPublisher;
    private final CodeGeneratorRegistry registry;
    private boolean simulateMongoFailure = false;

    // Available States
    private final ActiveState activeState;
    private final MaintenanceState maintenanceState;

    // The current internal state
    private UrlServiceState currentState;

    @PostConstruct
    public void init() {
        this.currentState = activeState; // Default state
    }

    public void toggleMongoFailure(boolean fail) {
        this.simulateMongoFailure = fail;
    }

    /**
     * Method to trigger a state change at runtime.
     */
    public void toggleMaintenanceMode(boolean enable) {
        this.currentState = enable ? maintenanceState : activeState;
        log.info("System state changed to: {}", currentState.getClass().getSimpleName());
    }

    @Override
    @Transactional
    public String shortenUrl(String originalUrl) {
        // Delegate to the current state
        return currentState.shorten(this, originalUrl);
    }

    public void saveToMongo(long id, String code, String url) {
        if (simulateMongoFailure) {
            log.error("SIMULATED ERROR: MongoDB is unreachable!");
            throw new RuntimeException("Connection timeout to MongoDB cluster");
        }
        MongoUrlMapping mongo = new MongoUrlMapping();
        mongo.setId(id);
        mongo.setShortCode(code);
        mongo.setTarget(url);

        Map<String, Object> extra = new HashMap<>();
        extra.put("createdAt", Instant.now().toString());
        extra.put("randomValue", new Random().nextDouble());

        if (id % 2 == 0) {
            extra.put("metadata", Map.of("source", "api", "priority", "high"));
        } else {
            extra.put("tags", new String[]{"url", "shorten", "test" + id});
        }

        mongo.setAdditionalData(extra);
        mongoRepo.save(mongo);
        log.info("Url data saved to MongoDB!");
    }

    @Override
    @Transactional
    public void deleteByCode(String code) {
        jpaRepo.deleteByShortCode(code);
        mongoRepo.deleteById(0L); // As noted previously, usually needs a real ID
    }

    @Override
    public String getOriginalUrlByCode(String code) {
        log.info("Fetching original URL for code: {}", code);
        return jpaRepo.findByShortCode(code)
                .map(UrlMapping::getTarget)
                .orElseThrow(() -> new NotFoundException("Mapping not found for code: " + code));
    }
}