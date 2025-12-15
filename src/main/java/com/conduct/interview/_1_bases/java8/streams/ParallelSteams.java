package com.conduct.interview._1_bases.java8.streams;

import java.util.List;

public class ParallelSteams {
    public static void main(String[] args) {
        List<Integer> integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        integerList.stream()
                .parallel()
                .forEach(System.out::println);
    }
}
