package org.design.designurlshortenergenerator.service.generator.api;

public interface UrlGeneratorService {
    String shortenUrl(String originalUrl);
    void deleteByCode(String code);
    String getOriginalUrlByCode(String code);
}