package com.conduct.interview._7_patterns.structural.adapter.check;

public class Main {
    public static void main(String[] args) {
        LegacyServiceImpl legacyService = new LegacyServiceImpl();
        NewService newService = new NewServiceAdapter(legacyService);
        WebController webController = new WebController(newService);
        webController.handleRequest();
    }
}
