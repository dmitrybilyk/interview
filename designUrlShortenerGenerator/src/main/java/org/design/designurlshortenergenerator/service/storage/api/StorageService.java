package org.design.designurlshortenergenerator.service.storage.api;

import org.design.designurlshortenergenerator.persistence.model.sql.UrlMapping;

public interface StorageService {
    void save(long id, String code, String url);

    UrlMapping findByCode(String code);

    void saveMapping(UrlMapping mapping);
}