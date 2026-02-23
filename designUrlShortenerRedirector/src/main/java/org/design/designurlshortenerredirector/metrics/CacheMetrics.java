//package org.design.designurlshortenerredirector;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class CacheMetrics {
//
//    private final MeterRegistry meterRegistry;
//
//    @EventListener
//    public void handleCacheHit(CacheHitEvent event) {
//        if ("url-cache".equals(event.getCacheName())) {
//            meterRegistry.counter("cache.hit", "cache", event.getCacheName()).increment();
//        }
//    }
//
//    @EventListener
//    public void handleCacheMiss(CacheMissEvent event) {
//        if ("url-cache".equals(event.getCacheName())) {
//            meterRegistry.counter("cache.miss", "cache", event.getCacheName()).increment();
//        }
//    }
//}