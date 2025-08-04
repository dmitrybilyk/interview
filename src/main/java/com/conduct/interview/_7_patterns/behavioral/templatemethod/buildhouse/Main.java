package com.conduct.interview._7_patterns.behavioral.templatemethod.buildhouse;

public class Main {
    public static void main(String[] args) {
        HouseBuilder houseBuilder = HouseBuilderFactoryImpl.getInstance().buildHouse();
        houseBuilder.buildHouse();
    }
}
