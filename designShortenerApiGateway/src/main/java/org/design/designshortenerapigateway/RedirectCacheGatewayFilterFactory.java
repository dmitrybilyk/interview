//package org.design.designshortenerapigateway;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//@Component
//public class RedirectCacheGatewayFilterFactory
//        extends AbstractGatewayFilterFactory<Object> {
//
//    private final ReactiveStringRedisTemplate redisTemplate;
//    private final Duration ttl;
//
//    public RedirectCacheGatewayFilterFactory(
//            ReactiveStringRedisTemplate redisTemplate,
//            @Value("${gateway.cache.ttl-seconds:3600}") long ttlSeconds) {
//        this.redisTemplate = redisTemplate;
//        this.ttl = Duration.ofSeconds(ttlSeconds);
//    }
//
//    @Override
//    public GatewayFilter apply(Object config) {
//        return (exchange, chain) -> {
//            String path = exchange.getRequest().getURI().getPath();
//
//            if (!path.startsWith("/redirect/")) {
//                return chain.filter(exchange);
//            }
//
//            String shortCode = path.substring("/redirect/".length());
//            String cacheKey = "shorturl:" + shortCode;
//
//            return redisTemplate.opsForValue().get(cacheKey)
//                    .flatMap(cachedUrl -> {
//                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
//                        exchange.getResponse().getHeaders().set(HttpHeaders.LOCATION, cachedUrl);
//                        return exchange.getResponse().setComplete();
//                    })
//                    .switchIfEmpty(
//                            chain.filter(exchange)
//                                    .then(Mono.defer(() -> {
//                                        String location = exchange.getResponse()
//                                                .getHeaders()
//                                                .getFirst(HttpHeaders.LOCATION);
//                                        if (location != null) {
//                                            // Cache the redirect URL, ignore the boolean result
//                                            return redisTemplate.opsForValue()
//                                                    .set(cacheKey, location, ttl)
//                                                    .then(); // <- converts Mono<Boolean> to Mono<Void>
//                                        }
//                                        return Mono.empty();
//                                    }))
//                    );
//        };
//    }
//}
