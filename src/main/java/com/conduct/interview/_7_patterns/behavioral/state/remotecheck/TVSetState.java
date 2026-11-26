package com.conduct.interview._7_patterns.behavioral.state.remotecheck;

public class TVSetState {
    public void turnOn(TVSet tvSet) {
        throw new IllegalArgumentException("can't to to on");
    }
    public void turnOff(TVSet tvSet) {
        throw new IllegalArgumentException("can't to to off");
    }
    public void turnOnHold(TVSet tvSet) {
        throw new IllegalArgumentException("can't to to hold");
    }
}
