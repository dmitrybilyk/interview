package org.design.designurlshortenerredirector.service.rest.api;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

public interface RedirectService {
    Mono<String> resolve(String shortCode);
    void evict(String shortCode);
    void recordClick(String shortCode, HttpServletRequest request);
}