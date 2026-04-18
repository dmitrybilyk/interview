package com.conduct.interview._1_bases.java8.mapMethods;

import java.util.HashMap;
import java.util.Map;

public class PutIfAbsentTest {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("First", "ddd");
        map.put("Second", "bbb");

//        No need to check if key was already there
        map.putIfAbsent("First", "uuu");
        System.out.println(map.get("First"));
    }
}
