package com.conduct.interview._7_patterns.behavioral.state.remotecheck;

public class TVSet {
    private TVSetState state = new OffState();

    public void turnOn() {
        state.turnOn(this);
    }

    public void turnOff() {
        state.turnOff(this);
    }
    public void turnOnHold() {
        state.turnOnHold(this);
    }

    void changeState(TVSetState newState) {
        this.state = newState;
    }
}
