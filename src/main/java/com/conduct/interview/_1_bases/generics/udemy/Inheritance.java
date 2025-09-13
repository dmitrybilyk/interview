package com.conduct.interview._1_bases.generics.udemy;

import java.util.ArrayList;
import java.util.List;

public class Inheritance {
    public static void main(String[] args) {
//        List<String> names = new ArrayList<>();
//        names.add("John");
//        names.add("Jane");
//        printList(names);

        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        List<Double> doubles = new ArrayList<>();
        doubles.add(1.0);
        doubles.add(2.3);

        printList(integers);
        printList(doubles);

//        List<?> all = new ArrayList<>();
////        all.add(1);
////        all.add("dfd");
//        all.get(0);
//        all.get(1);
        List<String> foo;
//        printList(foo);


    }

    private static void printList(List<? extends Number> names) {
        names.forEach(System.out::println);
        Number number = names.get(0);
    }
}
