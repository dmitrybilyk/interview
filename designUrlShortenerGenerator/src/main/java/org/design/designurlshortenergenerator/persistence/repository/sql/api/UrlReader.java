package org.design.designurlshortenergenerator.persistence.repository.sql.api;

import org.design.designurlshortenergenerator.persistence.model.sql.UrlMapping;

import java.util.Optional;

public interface UrlReader {
    Optional<UrlMapping> findByShortCode(String code);
}