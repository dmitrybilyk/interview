package com.conduct.interview._1_bases.immutables_classes.why_string_is_immutable;

public class ForStringPool {
    public static void main(String[] args) {
        // Both 'str1' and 'str2' refer to the same object in the String Pool
        String str1 = "Hello";
        String str2 = "Hello";
        
        // Compare references (they point to the same object in the pool)
        System.out.println(str1 == str2); // true
        
        // Since strings are immutable, changing one doesn't affect the other
        String str3 = str1.toUpperCase(); // Creates a new String object
        str1 = str1.toUpperCase(); // even this creates new String object, just the same
//        reference remains
        System.out.println(str1);         // "Hello" (unchanged)
        System.out.println(str3);         // "HELLO" (new object)
    }
}
