package org.design.designurlshortenergenerator.service.storage.impl;

import lombok.RequiredArgsConstructor;
import org.design.designurlshortenergenerator.persistence.model.sql.UrlMapping;
import org.design.designurlshortenergenerator.persistence.repository.sql.UrlMappingRepository;
import org.design.designurlshortenergenerator.service.storage.api.StorageService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary // Default implementation
@RequiredArgsConstructor
public class SqlStorageService implements StorageService {
    private final UrlMappingRepository jpaRepo;

    @Override
    public void save(long id, String code, String url) {
        UrlMapping m = new UrlMapping();
        m.setId(id);
        m.setShortCode(code);
        m.setTarget(url);
        jpaRepo.saveMapping(m);
    }
}