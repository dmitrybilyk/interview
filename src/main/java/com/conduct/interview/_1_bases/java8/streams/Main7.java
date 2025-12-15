package com.conduct.interview._1_bases.java8.streams;

import java.util.*;

public class Main7 {
    public static void main(String[] args) {
//        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
//        numbers.stream()
//                .map(number -> number * number)
//                .toArray(Integer[]::new);
//
//        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eva");
//        Optional<String> max = names.stream()
//                .max(Comparator.comparing(String::length));
//        System.out.println(max.get().length());
//
//        List<String> sentences = Arrays.asList(
//                "Java Stream API provides a fluent interface for processing sequences of elements.",
//                "It supports functional-style operations on streams of elements, such as map-reduce transformations.",
//                "In this exercise, you need to count the total number of words in all sentences."
//        );
//
//        long collect = new HashSet<>(Stream.of(String.join(" ", sentences)
//                        .split(" "))
//                .toList())
//                .size();

                ;

//        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
//
//        List<String> list = words.stream()
//                .filter(s -> s.length() % 2 == 0)
//                .toList();
//
//        System.out.println(list);

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Integer collect = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(value -> value * value )
                .sum();
        System.out.println(collect);

    }
}
