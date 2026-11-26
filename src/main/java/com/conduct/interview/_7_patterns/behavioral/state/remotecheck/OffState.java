package com.conduct.interview._7_patterns.behavioral.state.remotecheck;

public class OffState extends TVSetState {
    @Override
    public void turnOn(TVSet tvSet) {
        System.out.println("turning on tv");
        tvSet.changeState(new OnState());
    }
}
