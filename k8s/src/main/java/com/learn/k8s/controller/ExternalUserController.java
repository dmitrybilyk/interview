package com.learn.k8s.controller;

import com.learn.k8s.RUser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;

import static java.lang.Thread.sleep;

@RestController
public class ExternalUserController {

    // Helper to simulate 500ms of "real" work
    private void simulateWork(int ms) {
        long end = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < end) {
            // "Busy wait" - keeps the CPU core pinned at 100%
            Math.sqrt(Math.random());
        }
    }

    @GetMapping("/users")
    public Flux<RUser> getRUsers() {
        return Mono.fromCallable(() -> {
                    simulateWork(1000); // Your heavy CPU task
                    return Arrays.asList(
                            new RUser("Dmytro", 44),
                            new RUser("Ruslan", 48)
                    );
                })
                .subscribeOn(Schedulers.boundedElastic()) // Offload the whole block
                .flatMapMany(Flux::fromIterable); // Turn the List into a Flux
    }

    @GetMapping("/users/premium")
    public Flux<RUser> getPremiumRUsers() {
        return Flux.defer(() -> {
            simulateWork(1000);
            return Flux.just(
                    new RUser("DmytroPrem", 44),
                    new RUser("RuslanPrem", 48)
            );
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/enrich/{name}")
    public Mono<RUser> enrichUser(@PathVariable String name) {
        return Mono.fromCallable(() -> {
            simulateWork(2000); // Heavy math here
            return new RUser(name + "-Enriched", 0);
        }).subscribeOn(Schedulers.boundedElastic()); // Move math to a background worker pool
    }
}