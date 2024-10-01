package com.conduct.interview._7_patterns.behavioral.state;// State Pattern Example: Traffic Light in a Single Java File

// State Interface
interface TrafficLightState {
    void switchLight();
}

// Concrete State: Red Light
class RedLightState implements TrafficLightState {
    private TrafficLightContext trafficLight;

    public RedLightState(TrafficLightContext trafficLight) {
        this.trafficLight = trafficLight;
    }

    @Override
    public void switchLight() {
        System.out.println("Switching from RED to GREEN.");
        trafficLight.setState(trafficLight.getGreenLightState());
    }
}

// Concrete State: Green Light
class GreenLightState implements TrafficLightState {
    private TrafficLightContext trafficLight;

    public GreenLightState(TrafficLightContext trafficLight) {
        this.trafficLight = trafficLight;
    }

    @Override
    public void switchLight() {
        System.out.println("Switching from GREEN to YELLOW.");
        trafficLight.setState(trafficLight.getYellowLightState());
    }
}

// Concrete State: Yellow Light
class YellowLightState implements TrafficLightState {
    private TrafficLightContext trafficLight;

    public YellowLightState(TrafficLightContext trafficLight) {
        this.trafficLight = trafficLight;
    }

    @Override
    public void switchLight() {
        System.out.println("Switching from YELLOW to RED.");
        trafficLight.setState(trafficLight.getRedLightState());
    }
}

// Context Class
class TrafficLightContext {
    private TrafficLightState redLightState;
    private TrafficLightState greenLightState;
    private TrafficLightState yellowLightState;

    private TrafficLightState currentState;

    public TrafficLightContext() {
        redLightState = new RedLightState(this);
        greenLightState = new GreenLightState(this);
        yellowLightState = new YellowLightState(this);
        
        currentState = redLightState; // Initial state is Red
    }

    public void setState(TrafficLightState state) {
        currentState = state;
    }

    public TrafficLightState getRedLightState() {
        return redLightState;
    }

    public TrafficLightState getGreenLightState() {
        return greenLightState;
    }

    public TrafficLightState getYellowLightState() {
        return yellowLightState;
    }

    public void switchLight() {
        currentState.switchLight();
    }
}

// Main Class
public class StatePatternExample {
    public static void main(String[] args) {
        TrafficLightContext trafficLight = new TrafficLightContext();

        // Simulate the traffic light behavior
        trafficLight.switchLight(); // Red to Green
        trafficLight.switchLight(); // Green to Yellow
        trafficLight.switchLight(); // Yellow to Red
        trafficLight.switchLight(); // Red to Green again
    }
}
