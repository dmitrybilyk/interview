package org.design.designurlshortenergenerator.persistance.mongo.model.repository;

import org.design.designurlshortenergenerator.persistance.mongo.model.MongoUrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUrlMappingRepository extends MongoRepository<MongoUrlMapping, Long> {
    // Optional: Add custom query methods if needed, e.g., findByShortCode(String shortCode);
}