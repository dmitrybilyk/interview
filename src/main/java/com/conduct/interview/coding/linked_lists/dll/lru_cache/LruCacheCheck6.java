package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.HashMap;
import java.util.Map;

public class LruCacheCheck6 {
    public static void main(String[] args) {
        LruCacheCheck6 lruCacheCheck6 = new LruCacheCheck6(3);
        lruCacheCheck6.put(2, 3);
        lruCacheCheck6.put(3, 4);
        lruCacheCheck6.put(4, 5);
        lruCacheCheck6.put(2, 9);
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
    private Map<Integer, Node> cache;
    private int capacity;
    private Node head;
    private Node tail;

    public LruCacheCheck6(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node != null) {
            moveToFront(node);
            return node.value;
        }
        return -1;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);
        if (node == null) {
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addToFront(newNode);
            capacity++;
        } else {
            node.value = value;
            moveToFront(node);
        }
        if (cache.size() > capacity) {
            Node prev = tail.prev;
            removeNode(prev);
            cache.remove(prev.key);
        }
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    private void removeNode(Node node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }

    private void addToFront(Node newNode) {
        newNode.prev = head;
        newNode.next = head.next;
        head.next = newNode;
        head.next.prev = newNode;
    }
}
