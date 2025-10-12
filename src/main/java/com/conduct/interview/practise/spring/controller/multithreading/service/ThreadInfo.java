package com.conduct.interview.practise.spring.controller.multithreading.service;

import java.util.Set;

public record ThreadInfo(
        String currentThread,
        int totalThreads,
        int uniqueThreads,
        Set<String> threadNames
) {}
