package com.conduct.interview._1_bases.java8.mapMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputeIfAbsentTest {
    public static void main(String[] args) {
        // in case there was not such key we init new list and in one row can add element
        Map<String, List<String>> map = new HashMap<>();
        map.computeIfAbsent("First", key -> {
                    System.out.println(key);
                    return new ArrayList<>();
                })
                .add("newElement");
        System.out.println(map);
    }
}
