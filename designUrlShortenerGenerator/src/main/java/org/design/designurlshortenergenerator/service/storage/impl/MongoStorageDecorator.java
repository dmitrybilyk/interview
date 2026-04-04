package org.design.designurlshortenergenerator.service.storage.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.design.designurlshortenergenerator.persistence.model.nosql.MongoUrlMapping;
import org.design.designurlshortenergenerator.persistence.model.sql.UrlMapping;
import org.design.designurlshortenergenerator.persistence.repository.nosql.MongoUrlMappingRepository;
import org.design.designurlshortenergenerator.service.storage.api.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("mongoDecorator")
@RequiredArgsConstructor
public class MongoStorageDecorator implements StorageService {
    
    private final StorageService delegate; // The SqlStorageService
    private final MongoUrlMappingRepository mongoRepo;

    @Override
    @CircuitBreaker(name = "mongoCircuit") // Move Resilience logic here!
    public void save(long id, String code, String url) {
        // 1. Do the core work (SQL)
        delegate.save(id, code, url);
        
        // 2. Add the decorated work (Mongo)
        MongoUrlMapping mongo = new MongoUrlMapping();
        mongo.setId(id);
        mongo.setShortCode(code);
        mongo.setTarget(url);
        mongoRepo.save(mongo);
    }

    @Override
    public UrlMapping findByCode(String code) {
        return null;
    }

    @Override
    public void saveMapping(UrlMapping mapping) {

    }
}