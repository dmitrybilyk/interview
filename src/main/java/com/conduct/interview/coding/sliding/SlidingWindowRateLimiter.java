package com.conduct.interview.coding.sliding;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final Deque<Long> timestamps;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.timestamps = new ArrayDeque<>();
    }

    public synchronized boolean allowRequest() {
        long now = Instant.now().toEpochMilli();
        long windowStart = now - windowSizeMillis;

        // 1️⃣ Remove old timestamps outside the window
        while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        // 2️⃣ Check if under limit
        if (timestamps.size() < maxRequests) {
            timestamps.addLast(now); // record this request
            return true;
        } else {
            return false; // reject if limit exceeded
        }
    }

    // optional helper to see how many requests currently counted
    public synchronized int currentCount() {
        return timestamps.size();
    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(500, 60_000); // 500 per minute

        // simulate requests
        for (int i = 1; i <= 505; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.println("Request #" + i + " allowed? " + allowed);
            Thread.sleep(1); // small pause to simulate time passing
        }
    }
}
