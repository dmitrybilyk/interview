package com.conduct.interview._3_spring.bean_lifecycle.phases._9Custom_Init_Method;

import lombok.Getter;

@Getter
public class CustomInitMethod {
    private String name;

    public void customInit() {
        System.out.println("Custom Initialization: Executing custom init for " + getName());
        performSpecialTraining();
    }

    private void performSpecialTraining() {
        System.out.println("Custom Initialization: " + getName() + " is performing a special training routine.");
    }
}
