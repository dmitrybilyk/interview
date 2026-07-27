package com.conduct.interview.coding.linked_lists.dll.lru_cache.concurrent;

import java.util.HashMap;
import java.util.Map;

public class BrokenLRUTest {

    static class LRU {

        class Node {
            int key, value;
            Node prev, next;

            Node(int k, int v) {
                key = k;
                value = v;
            }
        }

        private final int capacity;
        private final Map<Integer, Node> cache = new HashMap<>();
        private final Node head = new Node(0, 0);
        private final Node tail = new Node(0, 0);

        public LRU(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            Node node = cache.get(key);
            if (node == null) return -1;

            moveToFront(node);
            return node.value;
        }

        public void put(int key, int value) {
            Node node = cache.get(key);

            if (node == null) {
                Node newNode = new Node(key, value);
                cache.put(key, newNode);
                addToFront(newNode);

                if (cache.size() > capacity) {
                    Node lru = tail.prev;
                    removeNode(lru);
                    cache.remove(lru.key);
                }
            } else {
                node.value = value;
                moveToFront(node);
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
    }

    public static void main(String[] args) throws InterruptedException {
        LRU cache = new LRU(5);

        Runnable task = () -> {
            for (int i = 0; i < 100_000; i++) {
                int key = i % 10;

                cache.put(key, i);
                cache.get(key);

                // трохи варіації
                if (i % 3 == 0) {
                    cache.put(key + 1, i);
                }
            }
        };

        // створюємо багато потоків
        Thread[] threads = new Thread[8];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Finished (якщо пощастило 😄)");
    }
}