package org.design.designurlshortenergenerator.service.messaging.api;

public interface UrlEventPublisher {
    void publishUrlCreated(String shortCode, String longUrl);
}