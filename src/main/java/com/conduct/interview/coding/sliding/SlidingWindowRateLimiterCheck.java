package com.conduct.interview.coding.sliding;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowRateLimiterCheck {
    public static void main(String[] args) {
        RateLimiterService rateLimiterService = new RateLimiterService(500, 60_000);
        for(int i = 0; i < 505; i++) {
            System.out.println("result is - " + rateLimiterService.checkLimits());
        }
    }
}

class RateLimiterService {
    private final int maxRequests;
    private final int timeWindow;
    private Deque<Long> timestamps = new ArrayDeque<>();

    public RateLimiterService(int maxRequests, int timeWindow) {
        this.maxRequests = maxRequests;
        this.timeWindow = timeWindow;
    }

    public boolean checkLimits() {
        long now = Instant.now().toEpochMilli();
        long earliestAllowedTime = now - timeWindow;

        while(!timestamps.isEmpty() && timestamps.peekFirst() < earliestAllowedTime) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < maxRequests) {
            timestamps.addLast(now);
            return true;
        } else {
            return false;
        }
    }
}
