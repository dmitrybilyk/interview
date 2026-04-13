package org.design.designurlshortenergenerator.service.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.persistence.repository.nosql.MongoUrlMappingRepository;
import org.design.designurlshortenergenerator.persistence.repository.sql.UrlMappingRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseDeleteListener {
    private final UrlMappingRepository jpaRepo;
    private final MongoUrlMappingRepository mongoRepo;

    @EventListener
    @Transactional
    public void handleDeletion(DeleteUrlCommand command) {
        log.info("Command Received: Deleting {} from Databases", command.code());
        jpaRepo.deleteByShortCode(command.code());
        // Add mongo deletion logic here
    }
}