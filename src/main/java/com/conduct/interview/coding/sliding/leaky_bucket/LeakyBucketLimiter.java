package com.conduct.interview.coding.sliding.leaky_bucket;

import java.util.LinkedList;
import java.util.Queue;

class LeakyBucketLimiter {
    private final int capacity;
    private final long leakIntervalMs;
    private final Queue<Long> bucket;
    private long lastLeakTimestamp;

    public LeakyBucketLimiter(int capacity, int requestsPerSecond) {
        this.capacity = capacity;
        this.leakIntervalMs = 1000 / requestsPerSecond;
        this.bucket = new LinkedList<>();
        this.lastLeakTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean validateLimit() {
        leak();

        if (bucket.size() < capacity) {
            bucket.add(System.currentTimeMillis());
            System.out.println("Request queued. Current bucket size: " + bucket.size());
            return true;
        } else {
            System.out.println("Disallowed. Bucket overflow (packet loss).");
            return false;
        }
    }

    private void leak() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastLeakTimestamp;
        
        // Calculate how many requests "leaked out" since the last check
        long leakedItems = elapsed / leakIntervalMs;

        if (leakedItems > 0) {
            for (int i = 0; i < leakedItems && !bucket.isEmpty(); i++) {
                bucket.poll();
            }
            lastLeakTimestamp = now;
        }
    }
}