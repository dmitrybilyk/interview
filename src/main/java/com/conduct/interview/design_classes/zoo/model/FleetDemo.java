package com.conduct.interview.design_classes.zoo.model;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// --- Core Contract ---
interface Identifiable {
    String getId();
}

enum VehicleType {
    CAR, BUS, TRUCK
}

// --- Abstract Base ---
abstract class Vehicle implements Identifiable {
    private final String id;
    private final String model;
    private final int capacity;
    private final VehicleType type;

    protected Vehicle(String id, String model, int capacity, VehicleType type) {
        this.id = Objects.requireNonNull(id);
        this.model = Objects.requireNonNull(model);
        this.capacity = capacity;
        this.type = type;
    }

    @Override
    public String getId() { return id; }
    public String getModel() { return model; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return type + "{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}

// --- Subclasses ---
final class Car extends Vehicle {
    public Car(String id, String model, int capacity) {
        super(id, model, capacity, VehicleType.CAR);
    }
}

final class Bus extends Vehicle implements Routable {
    public Bus(String id, String model, int capacity) {
        super(id, model, capacity, VehicleType.BUS);
    }
}

final class Truck extends Vehicle implements Routable {
    private final int maxLoadKg;
    public Truck(String id, String model, int capacity, int maxLoadKg) {
        super(id, model, capacity, VehicleType.TRUCK);
        this.maxLoadKg = maxLoadKg;
    }
    public int getMaxLoadKg() { return maxLoadKg; }
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

interface FleetService<T extends Vehicle> {
    void register(T v);
    Optional<T> findById(String id);
    List<T> listAll();
}

class FleetServiceCarImpl implements FleetService<Car> {
    private final FleetRepository<Car> fleetRepository;

    public FleetServiceCarImpl(FleetRepository<Car> fleetRepository) {
        this.fleetRepository = fleetRepository;
    }

    public void register(Car v) {
        fleetRepository.save(v);
    }

    public Optional<Car> findById(String id) {
        return fleetRepository.findById(id);
    }

    public List<Car> listAll() {
        return fleetRepository.findAll();
    }

}

class FleetServiceBusImpl implements FleetService<Bus> {
    private final FleetRepository<Bus> fleetRepository;

    public FleetServiceBusImpl(FleetRepository<Bus> fleetRepository) {
        this.fleetRepository = fleetRepository;
    }

    public void register(Bus v) {
        fleetRepository.save(v);
    }

    public Optional<Bus> findById(String id) {
        return fleetRepository.findById(id);
    }

    public List<Bus> listAll() {
        return fleetRepository.findAll();
    }

}

interface FleetRepository<T extends Vehicle> {

    void save(T v);

    Optional<T> findById(String id);

    List<T> findAll();

}

interface CarFleetRepository extends FleetRepository<Car> {
    List<Car> findBySeats(int seats);
}

interface TruckFleetRepository extends FleetRepository<Truck> {
    List<Truck> findByLoadCapacity(int loadKg);
}

class InMemoryFleetRepository implements CarFleetRepository {
    private final Map<String, Car> vehicles = new ConcurrentHashMap<>();

    @Override
    public void save(Car v) {
        vehicles.put(v.getId(), v);
    }

    @Override
    public Optional<Car> findById(String id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    @Override
    public List<Car> findAll() {
        return List.copyOf(vehicles.values());
    }

    @Override
    public List<Car> findBySeats(int seats) {
        return List.of();
    }
}

class InMemoryBusRepository implements FleetRepository<Bus> {
    private final Map<String, Bus> vehicles = new ConcurrentHashMap<>();

    @Override
    public void save(Bus v) {
        vehicles.put(v.getId(), v);
    }

    @Override
    public Optional<Bus> findById(String id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    @Override
    public List<Bus> findAll() {
        return List.copyOf(vehicles.values());
    }

}

interface MaintenanceService<T extends Vehicle> {
    void performCheckup(T vehicle);
}

class CarMaintenanceService implements MaintenanceService<Car> {
    @Override
    public void performCheckup(Car car) {
        System.out.println("Performing oil check for car: " + car.getModel());
    }
}

interface Routable {
}

class TruckLogisticsService {
    public void calculateLoad(Truck truck, int cargoKg) {
        if (cargoKg <= truck.getMaxLoadKg()) {
            System.out.println("Truck " + truck.getModel() +
                    " can carry " + cargoKg + "kg safely.");
        } else {
            System.out.println("Overload! Max capacity is " + truck.getMaxLoadKg() + "kg.");
        }
    }
}

class RouteService {
    private final Map<String, String> assignments = new HashMap<>();

    public <T extends Vehicle & Routable> void assignRoute(T vehicle, String routeName) {
        assignments.put(vehicle.getId(), routeName);
        System.out.println(vehicle.getModel() +
                " assigned to route: " + routeName);
    }

    public <T extends Vehicle & Routable>Optional<String> getRoute(T vehicle) {
        return Optional.ofNullable(assignments.get(vehicle.getId()));
    }
}

class RoutableMaintenanceService {
    private final Map<String, LocalDate> maintenanceMap = new ConcurrentHashMap<>();

    public <T extends Vehicle & Routable> void scheduleMaintenance(
            LocalDate date,
            T vehicle) {
        maintenanceMap.put(vehicle.getId(), date);
    }

    public <T extends Vehicle & Routable> Optional<LocalDate> getMaintenanceDate(T vehicle) {
        return Optional.ofNullable(maintenanceMap.get(vehicle.getId()));
    }
}

// --- Demo ---
public class FleetDemo {
    public static void main(String[] args) {
        FleetRepository<Car> fleetRepository = new InMemoryFleetRepository();
        FleetService<Car> carFleetService = new FleetServiceCarImpl(fleetRepository);

        FleetRepository<Bus> busFleetRepository = new InMemoryBusRepository();
        FleetService<Bus> busFleetService = new FleetServiceBusImpl(busFleetRepository);

        Car car = VehicleFactory.createCar("Toyota Corolla", 5);
        Bus bus = VehicleFactory.createBus("Mercedes Sprinter", 20);
        Truck truck = VehicleFactory.createTruck("Volvo FH", 2, 12000);

        carFleetService.register(car);

        busFleetService.register(bus);

        MaintenanceService<Car> carMaintenanceService = new CarMaintenanceService();
        carMaintenanceService.performCheckup(car);

        RouteService routeService = new RouteService();
        routeService.assignRoute(bus, "To Kharkiv");
        routeService.assignRoute(truck, "To Kharkiv2");

        TruckLogisticsService truckLogisticsService = new TruckLogisticsService();
        truckLogisticsService.calculateLoad(truck, 2000);

        System.out.println("All vehicles: " + carFleetService.listAll());
        System.out.println("Find buses: " + busFleetService.listAll());

        RoutableMaintenanceService routableMaintenanceService = new RoutableMaintenanceService();
        routableMaintenanceService.scheduleMaintenance(
                LocalDate.now().plusDays(2),
                truck);
        routableMaintenanceService.scheduleMaintenance(
                LocalDate.now().plusDays(3),
                bus
        );

        Bus anotherBus = new Bus("newId", "Volvun", 50);

        routableMaintenanceService.getMaintenanceDate(anotherBus)
                .ifPresentOrElse(System.out::println,
                        () -> System.out.println("No value for - " + anotherBus.getId()));

    }
}
