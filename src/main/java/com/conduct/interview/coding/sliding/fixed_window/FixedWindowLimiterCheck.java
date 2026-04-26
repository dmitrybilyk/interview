package com.conduct.interview.coding.sliding.fixed_window;

public class FixedWindowLimiterCheck {
    private int maxRequests;
    private int counter;
    private long currentWindow;
    private long windowSize;

    FixedWindowLimiterCheck(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
        currentWindow = System.currentTimeMillis() / windowSize;
    }

    public boolean validateFixedWindow() {

        long newWindowKey = System.currentTimeMillis() / windowSize;
        if (newWindowKey != currentWindow) {
            currentWindow  = newWindowKey;
            counter = 0;
        }
        if (counter < maxRequests) {
            counter++;
            System.out.println("Allowed");
            return true;
        } else {
            System.out.println("Disallowed");
            return false;
        }
    }

    public static void main(String[] args) {
        FixedWindowLimiterCheck fixedWindowLimiterCheck = new FixedWindowLimiterCheck(500, 60_000);
        for (int i = 0; i < 505; i++) {
            fixedWindowLimiterCheck.validateFixedWindow();
        }
    }
}
