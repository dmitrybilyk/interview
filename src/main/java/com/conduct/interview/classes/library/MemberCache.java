package com.conduct.interview.classes.library;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemberCache {
    private final int capacity;
    private final LinkedHashMap<Integer, LibraryMember> cache;

    public MemberCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, LibraryMember> eldest) {
                return size() > capacity;
            }
        };
    }
}
