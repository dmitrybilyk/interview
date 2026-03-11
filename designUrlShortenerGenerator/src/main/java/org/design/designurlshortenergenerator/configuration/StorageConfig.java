package org.design.designurlshortenergenerator.configuration;

import org.design.designurlshortenergenerator.persistence.mongo.model.repository.MongoUrlMappingRepository;
import org.design.designurlshortenergenerator.persistence.repository.UrlMappingRepository;
import org.design.designurlshortenergenerator.service.storage.api.StorageService;
import org.design.designurlshortenergenerator.service.storage.impl.MongoStorageDecorator;
import org.design.designurlshortenergenerator.service.storage.impl.SqlStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public StorageService storageService(UrlMappingRepository jpa, MongoUrlMappingRepository mongo) {
        // We manually wrap the SQL service with the Mongo decorator
        StorageService sqlService = new SqlStorageService(jpa);
        return new MongoStorageDecorator(sqlService, mongo);
    }
}