package com.conduct.interview.design_classes.zoo.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

abstract class CivilVehicle extends Vehicle implements Passengerable {

    protected CivilVehicle(String id, String model, int capacity, VehicleType type) {
        super(id, model, capacity, type);
    }
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

    void doAnnualCheckup() {
        provideInfo();
        System.out.println("Annual checkup for Vehicle");
        System.out.println("price is - " + calculatePrice());
    }

    protected abstract void provideInfo();

    protected abstract int calculatePrice();

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
final class Car extends CivilVehicle {
    public Car(String id, String model, int capacity) {
        super(id, model, capacity, VehicleType.CAR);
    }

    @Override
    void doAnnualCheckup() {
        super.doAnnualCheckup();
        System.out.println("Annual checkup for Vehicle");
    }

    @Override
    protected void provideInfo() {
        System.out.println("info about Car");
    }

    @Override
    protected int calculatePrice() {
        return 500;
    }

    @Override
    public int getPassengersCount() {
        return 4;
    }

    @Override
    public void feedPassengers() {
        System.out.println("We are feeding of the car passengers - " + getPassengersCount());
    }
}

final class Bus extends CivilVehicle implements Routable {
    public Bus(String id, String model, int capacity) {
        super(id, model, capacity, VehicleType.BUS);
    }

    @Override
    public int getPassengersCount() {
        return 50;
    }

    @Override
    public void feedPassengers() {
        System.out.println("We are feeding passengers of the bus - " + getPassengersCount());
    }

    @Override
    protected void provideInfo() {
        System.out.println("providing info about Bus");
    }

    @Override
    protected int calculatePrice() {
        System.out.println("Calculating for bus");
        return 1000;
    }
}

abstract class Restaurant {
    void buySources() {
        System.out.println("buying source for food");
    }
    void cookFood() {
        System.out.println("cooking food");
    }
    abstract <T extends CivilVehicle> void doService(T t);
}

class CheapRestaurant extends Restaurant {

    @Override
    public void buySources() {
        super.buySources();
        System.out.println("really cheap");
    }

    @Override
    public void cookFood() {
        super.cookFood();
        System.out.println("low quality of food cooking");
    }

    @Override
    <T extends CivilVehicle> void doService(T t) {
        System.out.println("doing food service");
        t.feedPassengers();
    }

}

class ExpensiveRestaurant extends Restaurant {

    @Override
    public void buySources() {
        super.buySources();
        System.out.println("really best quality");
    }

    @Override
    public void cookFood() {
        super.cookFood();
        System.out.println("best quality of food cooking");
    }

    @Override
    <T extends CivilVehicle> void doService(T t) {
        System.out.println("doing best food service");
        t.feedPassengers();
    }

}

final class Truck extends Vehicle implements Routable {
    private final int maxLoadKg;
    public Truck(String id, String model, int capacity, int maxLoadKg) {
        super(id, model, capacity, VehicleType.TRUCK);
        this.maxLoadKg = maxLoadKg;
    }
    public int getMaxLoadKg() { return maxLoadKg; }


    @Override
    void doAnnualCheckup() {
        super.doAnnualCheckup();
        System.out.println("Annual checkup for Truck");
    }

    @Override
    protected void provideInfo() {
        System.out.println("Info about truck");
    }

    @Override
    protected int calculatePrice() {
        return 2000;
    }
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

interface ServiceProvider {
    <T> void register(ServiceKey<T> key, T service);
    <T> Optional<T> get(ServiceKey<T> key);
}

class SimpleServiceProvider implements ServiceProvider {
    private final Map<ServiceKey<?>, Object> services = new ConcurrentHashMap<>();

    @Override
    public <T> void register(ServiceKey<T> key, T service) {
        services.put(key, service);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(ServiceKey<T> key) {
        return Optional.ofNullable((T) services.get(key));
    }
}


abstract class ServiceKey<T> {
    private final Type type;

    protected ServiceKey() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("Missing type parameter.");
        }
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceKey<?> other)) return false;
        return Objects.equals(type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "ServiceKey<" + type.getTypeName() + ">";
    }
}

interface Passengerable {
    int getPassengersCount();
    void feedPassengers();
}

// --- Demo ---
public class FleetDemo {
    public static void main(String[] args) {
        ServiceProvider provider = new SimpleServiceProvider();

        FleetService<Car> carService = new FleetServiceCarImpl(new InMemoryFleetRepository());
        FleetService<Bus> busService = new FleetServiceBusImpl(new InMemoryBusRepository());
        MaintenanceService<Car> carMaintenance = new CarMaintenanceService();

        // Register by type
        provider.register(new ServiceKey<>() {
        }, carService);
        provider.register(new ServiceKey<>() {
        }, busService);
        provider.register(new ServiceKey<>() {
        }, carMaintenance);

        // Retrieve and use
        FleetService<Car> cs = provider.get(new ServiceKey<FleetService<Car>>() {}).orElseThrow();
        FleetService<Bus> bs = provider.get(new ServiceKey<FleetService<Bus>>() {}).orElseThrow();

        Car car = VehicleFactory.createCar("Toyota Corolla", 5);
        Bus bus = VehicleFactory.createBus("Mercedes Sprinter", 20);

        cs.register(car);
        bs.register(bus);

        car.doAnnualCheckup();

        System.out.println("Cars: " + cs.listAll());
        System.out.println("Buses: " + bs.listAll());

        Restaurant restaurant = new CheapRestaurant();
        restaurant.buySources();
        restaurant.cookFood();
        restaurant.doService(bus);
        restaurant.doService(car);

        bus.doAnnualCheckup();

        Restaurant goodRestaurant = new ExpensiveRestaurant();
        goodRestaurant.buySources();
        goodRestaurant.cookFood();
        goodRestaurant.doService(bus);
    }
}
