package com.conduct.interview._1_bases.java8;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Check {
    public static void main(String[] args) {
        List<Integer> integerList = Arrays.asList(3, 4, 5);

        Integer reduce = integerList.stream().reduce(5, (a, b) -> a > b ? a : b);
        System.out.println(reduce);

        Map<Boolean, List<Integer>> collect = integerList.stream().collect(Collectors.groupingBy(integer -> integer > 3));
        System.out.println(collect);

    }


}

class Student {

}