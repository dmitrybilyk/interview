package org.design.designurlshortenergenerator.service.generator.caching;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.design.designurlshortenergenerator.service.generator.api.UrlGeneratorService;
import org.design.designurlshortenergenerator.service.generator.impl.UrlGeneratorServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary // Tells Spring to inject this instead of the raw implementation
@RequiredArgsConstructor
public class UrlGeneratorCachingProxy implements UrlGeneratorService {

    private final UrlGeneratorServiceImpl delegate; // Your actual service
    private final Cache<String, String> caffeineCache; // Injected Caffeine bean

    @Override
    public String getOriginalUrlByCode(String code) {
        // 1. Check Cache
        String cachedUrl = caffeineCache.getIfPresent(code);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        // 2. If miss, call the real service
        String originalUrl = delegate.getOriginalUrlByCode(code);

        // 3. Populate cache for next time
        caffeineCache.put(code, originalUrl);
        
        return originalUrl;
    }

    // Pass-through methods (no caching needed for these)
    @Override
    public String shortenUrl(String originalUrl) {
        return delegate.shortenUrl(originalUrl);
    }

    @Override
    public void deleteByCode(String code) {
        caffeineCache.invalidate(code); // Evict from cache on delete!
        delegate.deleteByCode(code);
    }
}