package com.conduct.interview._6_message_brokers.kafka.manual.person;

public class Person {
    public String name;
    public int age;

    public Person() {} // Required for Jackson

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + '}';
    }
}
