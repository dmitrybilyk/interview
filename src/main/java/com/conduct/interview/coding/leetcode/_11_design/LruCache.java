package com.conduct.interview.coding.leetcode._11_design;

import java.util.HashMap;

// LC 146 — LRU Cache
// Задача: кеш з обмеженою місткістю, який при переповненні викидає
// НАЙДАВНІШЕ використаний елемент (Least Recently Used).
// Ідея: HashMap для O(1) доступу за ключем + двозв'язний список для O(1)
// переміщення елементів "вперед" (нещодавно використані) і видалення "хвоста" (найстаріші).
// head/tail — фіктивні (dummy) вузли-заглушки, що спрощують додавання/видалення на краях списку.
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
    // dummy-голова: head.next завжди вказує на НАЙНОВІШИЙ (найнедавніше використаний) елемент

    private Node tail;
    // dummy-хвіст: tail.prev завжди вказує на НАЙСТАРІШИЙ елемент — кандидата на видалення

    private int capacity;
    private HashMap<Integer, Node> cache;
    // ключ -> вузол, для миттєвого доступу без проходу по списку

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
        // спочатку список порожній: голова одразу з'єднана з хвостом
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
            // такого ключа немає в кеші
        }
        moveToFront(node);
        // звернулись до елемента -> він щойно став "найновіше використаним"

        return node.value;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);

        if (node != null) { // already exists
            node.value = value;   // update value
            moveToFront(node);    // refresh position
            // ключ уже був у кеші -> просто оновлюємо значення й піднімаємо нагору

        } else { // new entry
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addToFront(newNode);
            // новий ключ -> створюємо вузол, кладемо в мапу і на початок списку

            if (cache.size() > capacity) {
                Node lru = tail.prev;
                removeNode(lru);
                cache.remove(lru.key);
                // перевищили місткість -> видаляємо найстаріший елемент (перед tail)
                // і з мапи, і зі списку
            }
        }
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
        // "перемістити на початок" = спочатку вирізати вузол зі старого місця,
        // потім вставити його заново одразу після head
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        // класична вставка у двозв'язний список одразу після head (4 переприв'язки посилань)
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        // "зшиваємо" сусідів вузла між собою, сам вузол випадає зі списку
    }
}
