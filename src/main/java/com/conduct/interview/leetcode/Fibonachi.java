package com.conduct.interview.leetcode;

import java.util.stream.Stream;

public class Fibonachi {
    public static void main(String[] args) {
        printFibonici(5);
    }

    private static void printFibonici(int n) {
        Stream.iterate(new long[]{0, 1}, fib -> new long[]{fib[1], fib[0] + fib[1]})
                .limit(n)
                .map(fib -> fib[0])
                .forEach(System.out::println);
    }
}
