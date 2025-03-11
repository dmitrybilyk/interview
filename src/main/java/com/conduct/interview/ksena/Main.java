package com.conduct.interview.ksena;

public class Main {
    public static void main(String[] args) {
//        25.1.2024
        Baby baby = new Baby("Dmytro", "Bil", "1", 1, 2, 2024, 3000);
//        25.1.2024
        Date date = new Date(25, 1, 2024);
        System.out.println(baby.isWeightInValidRange(date));
    }
}
