package com.conduct.interview._7_patterns.behavioral.templatemethod.buildhouse;

public class TilesRoof implements Roof {
    @Override
    public void paintRoof() {
        System.out.println("Painting tiles roof");
    }
}
