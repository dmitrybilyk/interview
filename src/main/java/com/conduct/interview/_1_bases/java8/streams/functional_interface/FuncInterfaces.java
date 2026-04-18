package com.conduct.interview._1_bases.java8.streams.functional_interface;

import java.util.List;

public class FuncInterfaces {
    public static void main(String[] args) {

        Simulatable simulateFirst = input -> input.length() > 3;

        var list = List.of("First", "Second");
        list.stream().filter(simulateFirst::simulate).forEach(System.out::print);
    }
}

@FunctionalInterface
interface Simulatable {
    boolean simulate(String input);
}
