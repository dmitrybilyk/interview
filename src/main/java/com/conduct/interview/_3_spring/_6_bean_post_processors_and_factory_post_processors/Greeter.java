package com.conduct.interview._3_spring._6_bean_post_processors_and_factory_post_processors;

public class Greeter {

    private final String greeting;

    public Greeter(String greeting) {
        this.greeting = greeting;
    }

    public String greet() {
        return greeting;
    }
}
