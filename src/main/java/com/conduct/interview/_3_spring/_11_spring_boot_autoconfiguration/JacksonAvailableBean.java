package com.conduct.interview._3_spring._11_spring_boot_autoconfiguration;

public class JacksonAvailableBean {
    public String describe() {
        return "registered because com.fasterxml.jackson.databind.ObjectMapper is on the classpath";
    }
}
