package com.conduct.interview._1_bases.java8;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class All {
    public static void main(String[] args) {
        Optional<String> stringOptional = Optional.empty();
//        Optional<String> stringOptional = Optional.of("Hello");
//        System.out.println(stringOptional.orElse("World"));

//        String someStringObject = "dddd";
        String someStringObject = null;

//        System.out.println(Optional.of(someStringObject));
        System.out.println(Optional.ofNullable(someStringObject));

        List<String> list = new ArrayList<>();
        list.add("two");
        list.add("one");
        list.add("three");
        list.add("four");
        list.add("five");

        String ddd = list.stream()
                .reduce((s, s2) -> s + s2).get();
        System.out.println(ddd);

        list.stream().peek(System.out::println);

//        list.forEach(System.out::println);

//        list.stream().sorted(All::forMethodReference).forEach(System.out::println);

        Map<Character, List<String>> collect = list.stream().collect(Collectors.groupingBy(s -> s.charAt(0)));
        collect.entrySet().forEach(System.out::println);

    }

    public static Integer forMethodReference(String string, String string2) {
        return -1;
    }
}

