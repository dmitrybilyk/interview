package com.conduct.interview._1_bases.java8.mapMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Playground {
    public static void main(String[] args) {
        Map<String, List<String>> map = new HashMap<>();
        map.putIfAbsent("ONE", List.of("fff"));
        map.putIfAbsent("ONE", List.of("999"));

        System.out.println(map);
    }
}
