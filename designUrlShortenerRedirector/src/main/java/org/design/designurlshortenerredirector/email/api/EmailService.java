package org.design.designurlshortenerredirector.email.api;

import reactor.core.publisher.Mono;

// Example EmailService interface
public interface EmailService {
    Mono<Void> sendNotificationEmail(String shortCode, String originalUrl);
}