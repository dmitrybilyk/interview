package com.conduct.interview._7_patterns.creational.singleton.check;

public class StaticInnerSingleton {

    private StaticInnerSingleton() {

    }

    private static class SingletonHolder {
        static StaticInnerSingleton INSTANCE = new StaticInnerSingleton();
    }

    public static StaticInnerSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
