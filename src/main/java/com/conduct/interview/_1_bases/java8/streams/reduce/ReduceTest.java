package com.conduct.interview._1_bases.java8.streams.reduce;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ReduceTest {
    public static void main(String[] args) {
        var list = List.of(4, 5, 6, 8);
        System.out.println(list.stream().reduce(Integer::sum));
        System.out.println(list.stream().mapToInt(value -> value).sum());
        System.out.println(list.stream().reduce(10, Integer::sum));
        System.out.println(list.stream().reduce(10, Integer::sum, Integer::sum));

        Map<Integer, Integer> map = new HashMap<>();
        map.put(3, 5);
        map.forEach((key, value) -> System.out.println("" + key + value));

        Map<Integer, Integer> map2 = new HashMap<>();
        map2.put(3, 3);

        map2.merge(3, 3, (integer, integer2) -> integer + integer2);
    }
}
