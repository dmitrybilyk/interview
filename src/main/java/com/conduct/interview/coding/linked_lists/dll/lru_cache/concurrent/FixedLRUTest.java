package com.conduct.interview.coding.linked_lists.dll.lru_cache.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class FixedLRUTest {

    static class LRU {
        // Internal Node structure
        private static class Node {
            int key, value;
            Node prev, next;
            Node(int k, int v) {
                this.key = k;
                this.value = v;
            }
        }

        private final int capacity;
        private final Map<Integer, Node> cache;
        private final Node head;
        private final Node tail;
        
        // Use a ReentrantLock for better control than 'synchronized'
        private final ReentrantLock lock = new ReentrantLock();

        public LRU(int capacity) {
            this.capacity = capacity;
            this.cache = new HashMap<>();
            this.head = new Node(0, 0);
            this.tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            lock.lock();
            try {
                Node node = cache.get(key);
                if (node == null) {
                    return -1;
                }
                moveToFront(node);
                return node.value;
            } finally {
                // Critical: Always unlock in finally to prevent deadlocks
                lock.unlock();
            }
        }

        public void put(int key, int value) {
            lock.lock();
            try {
                Node node = cache.get(key);
                if (node == null) {
                    // New entry
                    Node newNode = new Node(key, value);
                    cache.put(key, newNode);
                    addToFront(newNode);

                    if (cache.size() > capacity) {
                        evict();
                    }
                } else {
                    // Update existing entry
                    node.value = value;
                    moveToFront(node);
                }
            } finally {
                lock.unlock();
            }
        }

        // --- Internal Helper Methods (Must be called while holding the lock) ---

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

        private void evict() {
            Node lru = tail.prev;
            if (lru != head) { // Safety check
                removeNode(lru);
                cache.remove(lru.key);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LRU cache = new LRU(5);
        int threadCount = 8;
        int operationsPerThread = 100_000;

        Runnable task = () -> {
            for (int i = 0; i < operationsPerThread; i++) {
                int key = i % 10;
                cache.put(key, i);
                cache.get(key);
                if (i % 3 == 0) {
                    cache.put(key + 1, i);
                }
            }
        };

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Finished! All operations completed safely.");
    }
}