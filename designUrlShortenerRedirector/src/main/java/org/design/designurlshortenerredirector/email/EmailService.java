package org.design.designurlshortenerredirector.email;

import reactor.core.publisher.Mono;

// Example EmailService interface
public interface EmailService {
    Mono<Void> sendNotificationEmail(String shortCode, String originalUrl);
}