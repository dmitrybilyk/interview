package org.design.designurlshortenergenerator.persistence.repository.api;

public interface UrlDeleter {
    void deleteByShortCode(String code);
}