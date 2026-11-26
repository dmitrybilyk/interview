package com.conduct.interview._7_patterns.behavioral.state.remotecheck;

public class OnHoldState extends TVSetState {
    @Override
    public void turnOn(TVSet tvSet) {
        super.turnOn(tvSet);
        System.out.println("from on hold to on");
    }

    @Override
    public void turnOff(TVSet tvSet) {
        super.turnOff(tvSet);
        System.out.println("from on hold to off");
    }
}
