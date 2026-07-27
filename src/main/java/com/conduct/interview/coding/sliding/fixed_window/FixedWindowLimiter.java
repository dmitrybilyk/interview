package com.conduct.interview.coding.sliding.fixed_window;

class FixedWindowLimiter {
    private final int maxRequests;
    private final long windowSizeMillis;
    
    private long currentWindowKey;
    private int counter;

    public FixedWindowLimiter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.currentWindowKey = System.currentTimeMillis() / windowSizeMillis;
        this.counter = 0;
    }

    public synchronized boolean validateLimit() {
        long now = System.currentTimeMillis();
        long windowKey = now / windowSizeMillis;

        // If the window has changed, reset the counter
        if (windowKey != currentWindowKey) {
            currentWindowKey = windowKey;
            counter = 0;
        }

        if (counter < maxRequests) {
            counter++;
            System.out.println("Allowed. Count: " + counter);
            return true;
        } else {
            System.out.println("Disallowed. Limit reached for window " + windowKey);
            return false;
        }
    }
}