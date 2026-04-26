package com.conduct.interview.coding.sliding.sliding_window_log;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowRateLimiterCheck2 {
    public static void main(String[] args) {
        RateLimiterValidator rateLimiterValidator = new RateLimiterValidator(500, 60_000);
        for (int i = 0; i < 505; i++) {
            rateLimiterValidator.validate();
        }
    }
}

class RateLimiterValidator {
    private long maxRequests;
    private long windowSize;
    private Deque<Long> timestamps;
    RateLimiterValidator(long maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
        timestamps = new ArrayDeque<>();
    }

    public boolean validate() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize;
        while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < maxRequests) {
            timestamps.add(System.currentTimeMillis());
            System.out.println("allowed");
            return true;
        } else  {
            System.out.println("exceeded!!!");
            return false;
        }
    }
}
