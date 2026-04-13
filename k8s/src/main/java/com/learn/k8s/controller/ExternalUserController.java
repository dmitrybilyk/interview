package com.learn.k8s.controller;

import com.learn.k8s.RUser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class ExternalUserController {

    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    private final int totalCores = Runtime.getRuntime().availableProcessors();

    private void simulateWork(int ms) {
        long end = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < end) {
            // Busy wait pins the CPU core
            Math.sqrt(Math.random());
        }
    }

    private void logDiagnostics(String phase, String method, String name) {
        long timestamp = System.currentTimeMillis();
        int currentBusy = activeTasks.get();
        double load = osBean.getSystemLoadAverage();
        String thread = Thread.currentThread().getName();

        System.out.printf("[%d] %-6s | %-12s | ActiveTasks: %d/%d | LoadAvg: %.2f | Thread: %s | User: %s%n",
                timestamp, phase, method, currentBusy, totalCores, load, thread, name);
    }

    @GetMapping("/users")
    public Flux<RUser> getRUsers() {
        return Mono.fromCallable(() -> {
                    activeTasks.incrementAndGet();
                    logDiagnostics("START", "getUsers", "Regular");
                    simulateWork(1000);
                    List<RUser> users = Arrays.asList(
                            new RUser("Dmytro", 44),
                            new RUser("Dmytro2", 44),
                            new RUser("Dmytro3", 44),
                            new RUser("Dmytro4", 44),
                            new RUser("Ruslan", 48)
                    );
                    logDiagnostics("END", "getUsers", "Regular");
                    activeTasks.decrementAndGet();
                    return users;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/users/premium")
    public Flux<RUser> getPremiumRUsers() {
        return Mono.fromCallable(() -> {
                    activeTasks.incrementAndGet();
                    logDiagnostics("START", "getUsers", "Premium");
                    simulateWork(1000);
                    List<RUser> users = Arrays.asList(new RUser("DmytroPremium", 44), new RUser("RuslanPremium", 48));
                    logDiagnostics("END", "getUsers", "Premium");
                    activeTasks.decrementAndGet();
                    return users;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/enrich/{name}")
    public Mono<RUser> enrichUser(@PathVariable String name) {
        return Mono.fromCallable(() -> {
            activeTasks.incrementAndGet();
            logDiagnostics("START", "enrich", name);
            simulateWork(2000);
            logDiagnostics("END", "enrich", name);
            activeTasks.decrementAndGet();
            return new RUser(name + "-Enriched", 0);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}