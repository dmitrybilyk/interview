package com.conduct.interview._1_bases.java8.mapMethods;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputeIfPresentTest {
    public static void main(String[] args) {
        Map<String, List<String>> stringStringMap = new HashMap<>();
        stringStringMap.put("First", Arrays.asList("ddd"));
        stringStringMap.put("Second", Arrays.asList("sss"));
        // Lambda will be executed just in case there is already such a key in map
        stringStringMap.computeIfPresent("First2", (key, value) -> {
            List<String> result = value.stream().map(s -> s + s).toList();
            return result;
        });
        System.out.println(stringStringMap);
    }
}
