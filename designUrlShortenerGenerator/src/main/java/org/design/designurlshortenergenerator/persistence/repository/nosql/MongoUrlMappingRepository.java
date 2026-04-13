package org.design.designurlshortenergenerator.persistence.repository.nosql;

import org.design.designurlshortenergenerator.persistence.model.nosql.MongoUrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUrlMappingRepository extends MongoRepository<MongoUrlMapping, Long> {
    // Optional: Add custom query methods if needed, e.g., findByShortCode(String shortCode);
}