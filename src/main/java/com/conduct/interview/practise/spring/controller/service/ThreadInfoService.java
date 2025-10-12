package com.conduct.interview.practise.spring.controller.service;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ThreadInfoService {

    // stores names of all threads that handled requests
    private final Set<String> requestThreads =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ThreadInfo handleRequest() {
        String threadName = Thread.currentThread().getName();
        requestThreads.add(threadName);

        int totalThreads = ManagementFactory.getThreadMXBean().getThreadCount();

        return new ThreadInfo(
                threadName,
                totalThreads,
                requestThreads.size(),
                requestThreads
        );
    }
}
