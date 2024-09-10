package com.conduct.interview._7_patterns.creational.factory.factory_method;

public class FactoryMethodVehicleCheck {
    public static void main(String[] args) {
        VehicleType vehicleType = VehicleType.CAR;
        RaceClient raceClient = new RaceClient();
        if (VehicleType.CAR == vehicleType) {
            raceClient.setRaceVehicleFactory(new CarRaceVehicleFactory());
        } else {
            raceClient.setRaceVehicleFactory(new MotoRaceVehicleFactory());
        }
        raceClient.showIsStarting();
    }
}

class RaceClient {
    private RaceVehicleFactory raceVehicleFactory;

    public void showIsStarting() {
        Vehicle vehicle = raceVehicleFactory.createVehicle();
        vehicle.drive();
    }

    public void setRaceVehicleFactory(RaceVehicleFactory raceVehicleFactory) {
        this.raceVehicleFactory = raceVehicleFactory;
    }
}

interface RaceVehicleFactory {
    Vehicle createVehicle();
}

class CarRaceVehicleFactory implements RaceVehicleFactory {

    @Override
    public Vehicle createVehicle() {
        return new Car();
    }
}

class MotoRaceVehicleFactory implements RaceVehicleFactory {

    @Override
    public Vehicle createVehicle() {
        return new MotoBike();
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

