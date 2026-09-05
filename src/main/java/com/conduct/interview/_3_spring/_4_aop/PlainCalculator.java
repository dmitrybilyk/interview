package com.conduct.interview._3_spring._4_aop;

/** No interface -> Spring AOP must fall back to a CGLIB subclass proxy. */
public class PlainCalculator {
    public int add(int a, int b) {
        return a + b;
    }
}
