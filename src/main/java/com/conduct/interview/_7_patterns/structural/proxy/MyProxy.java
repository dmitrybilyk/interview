package com.conduct.interview._7_patterns.structural.proxy;

public class MyProxy {
    public static void main(String[] args) {
//        SomeInterface someInterface = new SomeProxy(new SomeClass(), "not null");
        SomeInterface someInterface = new SomeProxy(new SomeClass(), null);
        someInterface.someMethod();
    }
}

interface SomeInterface {
    void someMethod();
}

class SomeClass implements SomeInterface {

    @Override
    public void someMethod() {
        System.out.println("ddd");
    }
}

class SomeProxy implements SomeInterface {
    private SomeInterface someInterface;
    private String someString;

    public SomeProxy(SomeInterface someInterface, String someString) {
        this.someInterface = someInterface;
        this.someString = someString;
    }

    @Override
    public void someMethod() {
        if (someString != null) {
            System.out.println("Additional logic");
        }
        someInterface.someMethod();
    }
}