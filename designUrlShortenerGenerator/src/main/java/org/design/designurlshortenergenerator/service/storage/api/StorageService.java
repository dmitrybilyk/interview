package org.design.designurlshortenergenerator.service.storage.api;

public interface StorageService {
    void save(long id, String code, String url);
}