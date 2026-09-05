package com.conduct.interview._3_spring._11_spring_boot_autoconfiguration;

public class SomeMissingLibraryBean {
    public String describe() {
        return "this would wrap a library that isn't actually on the classpath";
    }
}
