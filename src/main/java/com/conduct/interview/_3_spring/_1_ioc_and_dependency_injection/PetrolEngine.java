package com.conduct.interview._3_spring._1_ioc_and_dependency_injection;

public class PetrolEngine implements Engine {

    public PetrolEngine() {
        System.out.println("PetrolEngine constructed (eagerly, at context refresh)");
    }

    @Override
    public String start() {
        return "vroom";
    }
}
