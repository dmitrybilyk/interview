package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.HashMap;
import java.util.Map;

public class LruCacheCheck2 {
    public static void main(String[] args) {
        LruCacheCheck2 lruCacheCheck2 = new LruCacheCheck2(5);
        lruCacheCheck2.put(10, 10);
        lruCacheCheck2.put(20, 20);
        lruCacheCheck2.put(30, 30);
        lruCacheCheck2.printAll();
        lruCacheCheck2.get(20);
        lruCacheCheck2.printAll();
    }

    private void put(int key, int value) {
        Node node = cache.get(key);
        if (node == null) {
            Node newNode = new Node(key, value);
            addToFront(newNode);
            cache.put(key, newNode);
            if (cache.size() > capacity) {
                Node oldest = tail.prev;
                removeNode(oldest);
                cache.remove(oldest.key);
            }
        } else {
            node.value = value;
            moveToFront(node);
        }
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        } else {
            moveToFront(node);
            return node.value;
        }
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    private Map<Integer, Node> cache;
    private final int capacity;
    private Node head;
    private Node tail;

    public LruCacheCheck2(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        this.head.next = tail;
        this.tail.prev = head;
    }

    class Node {
        int key;
        int value;
        Node next;
        Node prev;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public void printAll() {
        Node current = head.next;
        while (current != tail) {
            System.out.println(current.key + " - " + current.value);
            current = current.next;
        }
    }
}
