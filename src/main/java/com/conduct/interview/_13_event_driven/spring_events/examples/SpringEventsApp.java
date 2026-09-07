package com.conduct.interview._13_event_driven.spring_events.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Run this to see all three listener styles in action.
 * No external infrastructure needed — purely in-process.
 *
 * Expected output order:
 *   1. SYNC listeners (placeOrder) — same thread, blocking
 *   2. "shipOrder() returned" — then ASYNC listener logs appear from a pool thread
 *   3. BEFORE_COMMIT, then AFTER_COMMIT listeners (cancelOrder) — after TX commits
 */
@SpringBootApplication(scanBasePackages = "com.conduct.interview._13_event_driven.spring_events")
@EnableAsync  // required for @Async listeners
public class SpringEventsApp {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringEventsApp.class, args);
        OrderService orderService = ctx.getBean(OrderService.class);

        System.out.println("\n=== 1. SYNC event ===");
        orderService.placeOrder("order-1", 299.99);

        System.out.println("\n=== 2. ASYNC event ===");
        orderService.shipOrder("order-1", "TRACK-XYZ-123");
        System.out.println("[main] Returned from shipOrder — async listener is running in background");

        System.out.println("\n=== 3. TRANSACTIONAL event ===");
        orderService.cancelOrder("order-1", "customer request");

        Thread.sleep(1500);  // wait for async listener to finish
        ctx.close();
    }
}
