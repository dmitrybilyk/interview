package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LruCacheCheck8 {
    static class Node {
        private int key;
        private int value;
        private Node prev;
        private Node next;
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head;
    private final Node tail;
    private final Lock lock = new ReentrantLock();

    public LruCacheCheck8(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        try {
            lock.lock();
            final Node node = cache.get(key);
            if (node != null) {
                moveToFront(node);
                return node.value;
            } else {
                return -1;
            }
        } finally {
            lock.unlock();
        }
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }
        try {
            lock.lock();
            Node node = cache.get(key);
            if (node != null) {
                node.value = value;
                moveToFront(node);
            } else {
                Node newNode = new Node(key, value);
                addToFront(newNode);
                cache.put(key, newNode);
                if (cache.size() > capacity) {
                    Node lastNode = tail.prev;
                    removeNode(lastNode);
                    cache.remove(lastNode.key);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }
}
