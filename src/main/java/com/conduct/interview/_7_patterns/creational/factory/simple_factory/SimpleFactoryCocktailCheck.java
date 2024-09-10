package com.conduct.interview._7_patterns.creational.factory.simple_factory;

public class SimpleFactoryCocktailCheck {
    public static void main(String[] args) {
        VehicleType vehicleType = VehicleType.CAR;
        Vehicle vehicle = createVehicle(vehicleType);
        vehicle.drive();
    }

    private static Vehicle createVehicle(VehicleType vehicleType) {
        Vehicle vehicle;
        if (VehicleType.CAR == vehicleType) {
            vehicle =  new Car();
        } else {
            vehicle = new MotoBike();
        }
        return vehicle;
    }
}

interface Vehicle {
    void drive();
}

class Car implements Vehicle {

    @Override
    public void drive() {
        System.out.println("driving a car");
    }
}

class MotoBike implements Vehicle {

    @Override
    public void drive() {
        System.out.println("driving a bike");
    }
}

enum VehicleType {
    CAR,
    MOTO_BIKE
}
