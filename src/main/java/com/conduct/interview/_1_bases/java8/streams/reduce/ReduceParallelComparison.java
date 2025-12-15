package com.conduct.interview._1_bases.java8.streams.reduce;

import java.util.List;
import java.util.stream.IntStream;

public class ReduceParallelComparison {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 100_000_000)
                .boxed()
                .toList();

        System.out.println("List size: " + numbers.size());

        // warm-up JVM for more realistic measurement
        numbers.stream().reduce(0, Integer::sum);

        measure("Sequential reduce", () -> {
            int sum = numbers.stream().reduce(0, Integer::sum);
            System.out.println("Sum = " + sum);
        });

        measure("Parallel reduce", () -> {
            int sum = numbers.parallelStream().reduce(0, Integer::sum);
            System.out.println("Sum = " + sum);
        });

        measure("Sequential sum()", () -> {
            long sum = numbers.stream().mapToInt(Integer::intValue).sum();
            System.out.println("Sum = " + sum);
        });

        measure("Parallel sum()", () -> {
            long sum = numbers.parallelStream().mapToInt(Integer::intValue).sum();
            System.out.println("Sum = " + sum);
        });
    }

    private static long measure(String name, Runnable action) {
        long start = System.currentTimeMillis();
        action.run();
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println(name + " took " + time + " ms\n");
        return time;
    }
}
