package com.conduct.interview.practise.spring.controller.multithreading.service.lists;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SyncedListService {

    private final List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());

    /** Add items to the synchronized list */
    public void addItems(int items) {
        for (int i = 0; i < items; i++) {
            syncList.add(i); // Thread-safe
        }
    }

    /** Return current size */
    public int getSize() {
        return syncList.size();
    }

    /** Clear list */
    public void clear() {
        syncList.clear();
    }
}
