package com.conduct.interview._20_algorythms_and_data_structures.linked_lists;

import java.util.HashMap;
import java.util.Map;

public class LruCacheCheck {

    private Map<Integer, LinkedListNode> cache;
    private final int maxSize;
    private LinkedListNode head;
    private LinkedListNode tail;

    LruCacheCheck(int maxSize) {
        this.maxSize = maxSize;
        cache = new HashMap<>();
    }

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

//    public String getValue(int key) {
//        LinkedListNode linkedListNode = cache.get(key);
//
//    }

    public void setKeyValue(int key, String value) {
        if (cache.size() >= maxSize) {
            removeTailNode();
        }
        LinkedListNode node = new LinkedListNode(key, value);
        addNodeToFront(node);
        cache.put(key, node);

    }

    private void addNodeToFront(LinkedListNode node) {
        if (head == null) {
            head = node;
            tail = node;
        } else {
            head.prev = node;
            node.next = head;
            head = node;
        }
    }

    public boolean removeTailNode() {
        tail.prev.next = null;
        tail = tail.prev;
        return true;
    }

}
