package com.conduct.interview._7_patterns.creational.singleton.check;

public enum SingletonEnum {
    INSTANCE;

    int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int doubleAge() {
        return this.age * 2;
    }
}
