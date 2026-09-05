package com.conduct.interview._3_spring._1_ioc_and_dependency_injection;

public class Car {

    // constructor injection: final, can never be half-wired
    private final Engine engine;

    // setter injection: optional/secondary dependency
    private int topSpeedKmh = 180;

    public Car(Engine engine) {
        System.out.println("Car constructed with " + engine.getClass().getSimpleName());
        this.engine = engine;
    }

    public void setTopSpeedKmh(int topSpeedKmh) {
        this.topSpeedKmh = topSpeedKmh;
    }

    public String drive() {
        return engine.start() + " - cruising at up to " + topSpeedKmh + " km/h";
    }
}
