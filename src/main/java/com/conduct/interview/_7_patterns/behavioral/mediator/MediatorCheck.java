package com.conduct.interview._7_patterns.behavioral.mediator;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MediatorCheck {
    public static void main(String[] args) {
        DispatcherMediator mediator = new DispatcherMediatorImpl();
        mediator.addFlight(new FlightImpl("Flight 1", mediator));
        FlightImpl flight2 = new FlightImpl("Flight 2", mediator);
        mediator.addFlight(flight2);
        mediator.addFlight(new FlightImpl("Flight 3", mediator));
        flight2.sendMessage(flight2, "Message from flight 2");
    }
}




interface Flight {
    void receiveMessage(String message);
    void sendMessage(Flight flight2, String message);
}

@Getter
@Setter
class FlightImpl implements Flight {
    private String name;
    private DispatcherMediator mediator;

    public FlightImpl(String name, DispatcherMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public void receiveMessage(String message) {
        System.out.println("Flight " + name + " received the message " + message);
    }
    public void sendMessage(Flight flight, String message) {
        mediator.sendMessage(flight, message);
    }
}

interface DispatcherMediator {
    void sendMessage(Flight flight, String message);
    void addFlight(Flight flight);
}

class DispatcherMediatorImpl implements DispatcherMediator {
    private final List<Flight> flightImplList = new ArrayList<>();
    public void sendMessage(Flight flight, String message) {
        for (Flight flightImpl : flightImplList) {
            if (!flightImpl.equals(flight)) {
                flightImpl.receiveMessage(message);
            }
        }
    }

    @Override
    public void addFlight(Flight flight) {
        flightImplList.add(flight);
    }
}