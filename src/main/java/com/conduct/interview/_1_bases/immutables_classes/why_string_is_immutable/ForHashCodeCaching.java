package com.conduct.interview._1_bases.immutables_classes.why_string_is_immutable;

import java.util.HashMap;

public class ForHashCodeCaching {
    public static void main(String[] args) {
        // Create a HashMap to store strings
        HashMap<String, String> map = new HashMap<>();
        
        // Add a key-value pair to the map
        String key = "hello";
        map.put(key, "world");

        // Retrieve the value using the key
        System.out.println("Value for 'hello': " + map.get("hello")); // Output: world
        
        // Since String is immutable, its hashCode is cached and consistent
        System.out.println("HashCode of 'hello': " + key.hashCode());
        
        // Even after multiple calls, hashCode doesn't change
        System.out.println("HashCode of 'hello' (again): " + key.hashCode());
    }
}
