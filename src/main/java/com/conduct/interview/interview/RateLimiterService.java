package com.conduct.interview.interview;

public class RateLimiterService {

    static int numberOfAttempts = 500;

    static long currentTime = System.currentTimeMillis();


    public static boolean getResult() {

        long startTime = System.currentTimeMillis() / 1000 - 60;

        if ((System.currentTimeMillis() / 1000  - startTime ) < 60 && numberOfAttempts > 0) {
            numberOfAttempts--;
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        getResult();
    }
}
