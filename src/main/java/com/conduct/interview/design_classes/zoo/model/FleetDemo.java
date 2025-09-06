package com.conduct.interview.design_classes.zoo.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// --- Core Contract ---
interface Identifiable {
    String getId();
}

// --- Abstract Base ---
abstract class Vehicle implements Identifiable {
    private final String id;
    private final String model;
    private final int capacity;

    protected Vehicle(String id, String model, int capacity) {
        this.id = Objects.requireNonNull(id);
        this.model = Objects.requireNonNull(model);
        this.capacity = capacity;
    }

    @Override
    public String getId() { return id; }
    public String getModel() { return model; }
    public int getCapacity() { return capacity; }

    // Abstract method for subclasses
    public abstract String getType();

    @Override
    public String toString() {
        return getType() + "{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}

// --- Subclasses ---
final class Car extends Vehicle {
    public Car(String id, String model, int capacity) {
        super(id, model, capacity);
    }
    @Override public String getType() { return "Car"; }
}

final class Bus extends Vehicle {
    public Bus(String id, String model, int capacity) {
        super(id, model, capacity);
    }
    @Override public String getType() { return "Bus"; }
}

final class Truck extends Vehicle {
    private final int maxLoadKg;
    public Truck(String id, String model, int capacity, int maxLoadKg) {
        super(id, model, capacity);
        this.maxLoadKg = maxLoadKg;
    }
    public int getMaxLoadKg() { return maxLoadKg; }
    @Override public String getType() { return "Truck"; }
}

// --- Factory ---
class VehicleFactory {
    private static final AtomicInteger COUNTER = new AtomicInteger();

    public static Car createCar(String model, int capacity) {
        return new Car(newId(), model, capacity);
    }

    public static Bus createBus(String model, int capacity) {
        return new Bus(newId(), model, capacity);
    }

    public static Truck createTruck(String model, int capacity, int maxLoadKg) {
        return new Truck(newId(), model, capacity, maxLoadKg);
    }

    private static String newId() {
        return "V-" + COUNTER.incrementAndGet();
    }
}

// --- Service ---
class FleetService {
    private final Map<String, Vehicle> vehicles = new HashMap<>();

    public void register(Vehicle v) {
        vehicles.put(v.getId(), v);
    }

    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    public List<Vehicle> listAll() {
        return List.copyOf(vehicles.values());
    }

    public List<Vehicle> findByType(String type) {
        return vehicles.values().stream()
                .filter(v -> v.getType().equalsIgnoreCase(type))
                .toList();
    }
}

// --- Demo ---
public class FleetDemo {
    public static void main(String[] args) {
        FleetService service = new FleetService();

        Vehicle car = VehicleFactory.createCar("Toyota Corolla", 5);
        Vehicle bus = VehicleFactory.createBus("Mercedes Sprinter", 20);
        Vehicle truck = VehicleFactory.createTruck("Volvo FH", 2, 12000);

        service.register(car);
        service.register(bus);
        service.register(truck);

        System.out.println("All vehicles: " + service.listAll());
        System.out.println("Find bus: " + service.findByType("Bus"));
    }
}
