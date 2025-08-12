package com.conduct.interview._20_algorythms_and_data_structures.linked_lists;

import java.util.HashMap;
import java.util.Map;

public class LruCacheCheck {

    private Map<Integer, String> cache = new HashMap<>();

    private static class LinkedListNode {
        private int key;
        private String value;
        private LinkedListNode next;
        private LinkedListNode prev;

        public LinkedListNode(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public void addItem() {

    }

    public boolean removeItem() {
        return false;
    }

}
