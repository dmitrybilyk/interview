package com.conduct.interview.practise.spring.controller.multithreading.service.lists;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleListService {

    private final List<Integer> regularList = new ArrayList<>();

    /** Just add items to the list — thread-unsafe! */
    public void addItems(int items) {
        for (int i = 0; i < items; i++) {
            regularList.add(i); // Not thread-safe
        }
    }

    public int getSize() {
        return regularList.size();
    }

    public void clear() {
        regularList.clear();
    }
}
