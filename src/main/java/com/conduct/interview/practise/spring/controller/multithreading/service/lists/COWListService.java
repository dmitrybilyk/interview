package com.conduct.interview.practise.spring.controller.multithreading.service.lists;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class COWListService {

    private final List<Integer> cowList = new CopyOnWriteArrayList<>();

    /** Add items to the list */
    public void addItems(int items) {
        for (int i = 0; i < items; i++) {
            cowList.add(i); // Thread-safe
        }
    }

    public int getSize() {
        return cowList.size();
    }

    public void clear() {
        cowList.clear();
    }
}
