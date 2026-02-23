package org.design.designurlshortenergenerator.persistence.repository.api;

import org.design.designurlshortenergenerator.persistence.model.UrlMapping;

import java.util.Optional;

public interface UrlReader {
    Optional<UrlMapping> findByShortCode(String code);
}