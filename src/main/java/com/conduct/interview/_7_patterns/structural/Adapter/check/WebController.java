package com.conduct.interview._7_patterns.structural.adapter.check;

public class WebController {
    private final NewService newService;

    public WebController(NewService newService) {
        this.newService = newService;
    }

    public void handleRequest() {
        Long returnRate = newService.returnRate();
        System.out.println(returnRate);
    }
}
