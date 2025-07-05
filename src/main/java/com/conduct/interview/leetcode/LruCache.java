package com.conduct.interview.leetcode;

import java.util.HashMap;

public class LruCache {
    class Node {
        int key;
        int value;
        Node prev;
        Node next;
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    private Node head;
    private Node tail;
    private int capacity;
    private HashMap<Integer, Node> cache = new HashMap<>();

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    public int get(int key) {
        if (cache.containsKey(key)) {
            return -1;
        }
        Node node = cache.get(key);
        moveToFront(node);
        return node.value;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    private void addToFront(Node node) {
        head.next.prev = node;
        node.next = head.next;
        node.prev = head;
        head.next = node;

//        node.next = head.next;
//        node.prev = head;
//        head.next.prev = node;
//        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public void put(int key, int value) {
        Node node = new Node(key, value);
        if (cache.containsKey(key)) {
            cache.put(key, node);
            moveToFront(node);
        } else {
            cache.put(key, node);
            addToFront(node);
            if (cache.size() > capacity) {
                cache.remove(tail.prev.key);
                removeNode(tail.prev);
            }
        }

//        Node node = cache.get(key);
//        if (node != null) {
//            node.value = value;
//            moveToFront(node);
//        } else {
//            Node newNode = new Node(key, value);
//            cache.put(key, newNode);
//            addToFront(newNode);
//            if (cache.size() > capacity) {
//                Node lru = tail.prev;
//                removeNode(lru);
//                cache.remove(lru.key);
//            }
//        }


        cache.put(key, node);
    }
}
