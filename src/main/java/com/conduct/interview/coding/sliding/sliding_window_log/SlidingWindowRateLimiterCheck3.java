package com.conduct.interview.coding.sliding.sliding_window_log;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowRateLimiterCheck3 {
    public static void main(String[] args) {
        RequestsLimiter limiter = new RequestsLimiter(500, 60_000);

        for (int i = 0; i < 505; i++) {
            limiter.validateLimit();
        }
    }
}

class RequestsLimiter {
    private int maxRequests;
    private long timeWindow;
    private Deque<Long> timestamps;
    public RequestsLimiter(int maxRequests, long timeWindow) {
        this.maxRequests = maxRequests;
        this.timeWindow = timeWindow;
        this.timestamps = new ArrayDeque<>();
    }

    public boolean validateLimit() {
        long now = System.currentTimeMillis();
        long windowStart = now - timeWindow;

        while(!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < maxRequests) {
            System.out.println("Allowed");
            timestamps.add(System.currentTimeMillis());
            return true;
        } else {
            System.out.println("Disallowed");
            return false;
        }
    }
}
