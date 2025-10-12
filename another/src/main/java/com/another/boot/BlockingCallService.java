package com.another.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Service
public class BlockingCallService {

    private static final Logger log = LoggerFactory.getLogger(BlockingCallService.class);

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    // ⚠️ Tiny thread pool — will quickly saturate
    private final ExecutorService blockingPool = Executors.newFixedThreadPool(5);

    public BlockingCallService(RestTemplate restTemplate, WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    private static final String BASE_URL = "http://localhost:8080/interview";

    /** Blocking RestTemplate version */
    public void testWithRestTemplate(int requests) {
        long start = System.currentTimeMillis();
        log.info("=== Starting RestTemplate test with {} requests using {} threads ===",
                requests, ((ThreadPoolExecutor) blockingPool).getCorePoolSize());

        List<CompletableFuture<Void>> futures = IntStream.rangeClosed(1, requests)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    long t0 = System.currentTimeMillis();
                    try {
                        log.info("[REST-{}] Sending request on thread {}", i, Thread.currentThread().getName());
                        String result = restTemplate.getForObject(BASE_URL + "/starvation", String.class);
                        long elapsed = System.currentTimeMillis() - t0;
                        log.info("[REST-{}] ✅ Completed in {} ms (thread: {})", i, elapsed, Thread.currentThread().getName());
                    } catch (Exception e) {
                        log.error("[REST-{}] ❌ Failed: {}", i, e.getMessage());
                    } finally {
                        int active = ((ThreadPoolExecutor) blockingPool).getActiveCount();
                        log.debug("[REST-{}] Active threads in pool: {}", i, active);
                    }
                }, blockingPool))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long total = System.currentTimeMillis() - start;
        log.info("=== RestTemplate total time: {} ms ===", total);
    }

    /** Non-blocking WebClient version */
    /** Non-blocking WebClient version with thread names */
    public void testWithWebClient(int requests) {
        long start = System.currentTimeMillis();
        log.info("=== Starting WebClient test with {} requests ===", requests);

        List<Mono<String>> monos = IntStream.rangeClosed(1, requests)
                .mapToObj(i -> {
                    long t0 = System.currentTimeMillis();
                    return webClient.get()
                            .uri(BASE_URL + "/starvation")
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnSubscribe(sub -> log.info("[WEB-{}] Sending request (thread: {})", i, Thread.currentThread().getName()))
                            .doOnNext(res -> {
                                long elapsed = System.currentTimeMillis() - t0;
                                log.info("[WEB-{}] ✅ Completed in {} ms (thread: {})", i, elapsed, Thread.currentThread().getName());
                            })
                            .onErrorResume(e -> {
                                log.error("[WEB-{}] ❌ Failed: {} (thread: {})", i, e.getMessage(), Thread.currentThread().getName());
                                return Mono.empty();
                            });
                }).toList();

        Mono.when(monos).block();

        long total = System.currentTimeMillis() - start;
        log.info("=== WebClient total time: {} ms ===", total);
    }


    public void shutdown() {
        log.info("Shutting down blocking pool...");
        blockingPool.shutdown();
    }
}
