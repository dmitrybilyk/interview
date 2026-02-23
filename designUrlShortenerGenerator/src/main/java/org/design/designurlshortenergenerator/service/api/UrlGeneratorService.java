package org.design.designurlshortenergenerator.service.api;

public interface UrlGeneratorService {
    String shortenUrl(String originalUrl);
    void deleteByCode(String code);
}