package com.conduct.interview.coding.sliding.token_bucket;

import java.util.concurrent.TimeUnit;

class TokenBucketLimiter {
    private final long maxTokens;
    private final double refillRatePerMs;
    
    private long availableTokens;
    private long lastRefillTimestamp;

    public TokenBucketLimiter(long maxTokens, long refillRatePerWindow, long windowSizeMillis) {
        this.maxTokens = maxTokens;
        // Calculate how many tokens are added per single millisecond
        this.refillRatePerMs = (double) refillRatePerWindow / windowSizeMillis;
        this.availableTokens = maxTokens;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean validateLimit() {
        refill();

        if (availableTokens >= 1) {
            availableTokens--;
            System.out.println("Allowed. Tokens left: " + availableTokens);
            return true;
        } else {
            System.out.println("Disallowed. Bucket empty.");
            return false;
        }
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long durationSinceLastRefill = now - lastRefillTimestamp;
        
        // Calculate tokens gained over time: (time elapsed * tokens per ms)
        long tokensToAdd = (long) (durationSinceLastRefill * refillRatePerMs);

        if (tokensToAdd > 0) {
            availableTokens = Math.min(maxTokens, availableTokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}