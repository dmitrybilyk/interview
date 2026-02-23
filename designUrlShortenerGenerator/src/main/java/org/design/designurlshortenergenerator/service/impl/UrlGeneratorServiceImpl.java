package org.design.designurlshortenergenerator.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.generator.allocator.api.IdProvider;
import org.design.designurlshortenergenerator.generator.strategy.api.CodeGeneratorStrategy;
import org.design.designurlshortenergenerator.persistence.model.UrlMapping;
import org.design.designurlshortenergenerator.persistence.mongo.model.MongoUrlMapping;
import org.design.designurlshortenergenerator.persistence.mongo.model.repository.MongoUrlMappingRepository;
import org.design.designurlshortenergenerator.persistence.repository.UrlMappingRepository;
import org.design.designurlshortenergenerator.service.api.UrlGeneratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlGeneratorServiceImpl implements UrlGeneratorService {

    private final UrlMappingRepository jpaRepo;
    private final MongoUrlMappingRepository mongoRepo;
    private final IdProvider idProvider;
    private final CodeGeneratorStrategy codeGeneratorStrategy;

    @Override
    @Transactional
    public String shortenUrl(String originalUrl) {
        try {
            long id = idProvider.nextId();
            String code = codeGeneratorStrategy.encode(id);

            UrlMapping m = new UrlMapping();
            m.setId(id);
            m.setShortCode(code);
            m.setTarget(originalUrl);
            jpaRepo.saveMapping(m);
            log.info("Url is generated in SQL database!");

            saveToMongo(id, code, originalUrl);

            return code;
        } catch (Exception e) {
            log.error("Failed to generate URL", e);
            throw new RuntimeException("Error during URL shortening process", e);
        }
    }

    private void saveToMongo(long id, String code, String url) {
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
        long id = 0L;
        jpaRepo.deleteByShortCode(code);
        mongoRepo.deleteById(id);
    }

    @Override
    public String getOriginalUrlByCode(String code) {
        log.info("Fetching original URL for code: {}", code);

        return jpaRepo.findByShortCode(code)
                .map(UrlMapping::getTarget)
                .orElseThrow(() -> new NotFoundException("Mapping not found for code: " + code));
    }
}