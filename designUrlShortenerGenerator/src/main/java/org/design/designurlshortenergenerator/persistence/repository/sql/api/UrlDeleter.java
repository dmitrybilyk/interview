package org.design.designurlshortenergenerator.persistence.repository.sql.api;

public interface UrlDeleter {
    void deleteByShortCode(String code);
}