package com.conduct.interview.coding.linked_lists;

import java.util.HashMap;
import java.util.Map;

public class LRUCacheTest {
    private final int maxCacheSize;
    private final Map<Integer, LinkedListNode> cacheMap;
    private LinkedListNode head;
    private LinkedListNode tail;


    public LRUCacheTest(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        this.cacheMap = new HashMap<>();
    }

    public String getValue(int key) {
        LinkedListNode linkedListNode = cacheMap.get(key);

        if (linkedListNode == null) {
            return null;
        }

        removeNodeFromCache(linkedListNode);
        addNodeToFront(linkedListNode);
        return linkedListNode.value;
    }

    public void setKeyValue(int key, String value) {
        LinkedListNode node = new LinkedListNode(key, value);
        if (cacheMap.size() > maxCacheSize) {
            removeNodeFromCache(tail);
        }
        addNodeToFront(node);
        cacheMap.put(key, node);
    }

    private void addNodeToFront(LinkedListNode node) {
        if (head != null) {
            head.prev = node;
            node.next = head;
        }
        head = node;
    }

    private boolean removeNodeFromCache(LinkedListNode node) {
        if (node == tail) {
            tail.prev.next = null;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
        return true;
    }


    private class LinkedListNode {
        private int key;
        private String value;
        private LinkedListNode next;
        private LinkedListNode prev;

        public LinkedListNode(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
