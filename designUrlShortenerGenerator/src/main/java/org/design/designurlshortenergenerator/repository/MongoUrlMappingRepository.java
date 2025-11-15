package org.design.designurlshortenergenerator.repository;

import org.design.designurlshortenergenerator.model.MongoUrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUrlMappingRepository extends MongoRepository<MongoUrlMapping, Long> {
    // Optional: Add custom query methods if needed, e.g., findByShortCode(String shortCode);
}