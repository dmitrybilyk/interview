package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LruCacheCheck9 {
    private final int capacity;
    private final long globalTtl;
    private final Map<Integer, Node> cache;
    private final Node head;
    private final Node tail;
    private final Lock lock;
    public LruCacheCheck9(int capacity, int ttl) {
        this.capacity = capacity;
        this.globalTtl = ttl;
        cache = new HashMap<>();
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
        lock = new ReentrantLock();
    }

    public int get(int key) {
        try {
            lock.lock();
            Node node = cache.get(key);
            if (node != null) {
                moveToFront(node);
                return node.value;
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public void put(int key, int value) {
        try {
            lock.lock();
            Node node = cache.get(key);
            if (node != null) {
                moveToFront(node);
                node.value = value;
            } else {
                Node newNode = new Node(key, value);
                cache.put(key, newNode);
                addToFront(newNode);
                if (cache.size() > capacity) {
                    Node tailNode = tail.prev;
                    removeNode(tailNode);
                    cache.remove(tailNode.key);
                }
            }
            evictExpiredNodes();
        } finally {
            lock.unlock();
        }
    }

    private void evictExpiredNodes() {
        while (tail.prev != head) {
            Node current = tail.prev;
            if (System.currentTimeMillis() > current.expirationTime) {
                removeNode(current);
            } else {
                break;
            }
        }
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    private void addToFront(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    class Node {
        private final int key;
        private int value;
        private Node next;
        private Node prev;
        private long expirationTime;
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + globalTtl;
        }
    }

    public static void main(String[] args) {
        LruCacheCheck9 lruCacheCheck9 = new LruCacheCheck9(3, 1000 * 1000);
    }
}
