package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.HashMap;
import java.util.Map;

public class LruCacheCheck {
    private final int capacity;
    private Node head;
    private Node tail;
    private Map<Integer, Node> cache = new HashMap<>();
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

    public LruCacheCheck(int capacity) {
        this.capacity = capacity;
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        this.head.next = tail;
        this.tail.prev = head;
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

    public void put(int key, int value) {
        Node node = cache.get(key);
        if (node == null) {
            Node newNode = new Node(key, value);
            addToFront(newNode);
            cache.put(key, newNode);
            if (cache.size() > capacity) {
                cache.remove(this.tail.prev.key);
                removeNode(this.tail.prev);
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

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToFront(Node node) {
        node.next = this.head.next;
        node.prev = head;
        this.head.next.prev = node;
        this.head.next = node;
    }

    public void printAll() {
        Node current = this.head;
        while(current.next != null) {
            current = current.next;
            System.out.println(current.key + " - " + current.value);
        }
    }


    public static void main(String[] args) {
        LruCacheCheck lruCacheCheck = new LruCacheCheck(3);
        lruCacheCheck.put(1, 1);
        lruCacheCheck.put(2, 2);
        lruCacheCheck.put(3, 3);
        lruCacheCheck.printAll();
        System.out.println(lruCacheCheck.get(3));
        lruCacheCheck.printAll();
//        lruCacheCheck.put(40, 70);
//        System.out.println(lruCacheCheck.get(30));
//        lruCacheCheck.printAll();
    }
}
