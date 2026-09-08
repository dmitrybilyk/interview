package com.conduct.interview.coding.leetcode._4_stack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

// LC 20 — Valid Parentheses
// Pattern: Stack — push open brackets, pop and check on close
// Time O(n), Space O(n)

// Задача: перевірити, чи дужки в рядку правильно збалансовані/вкладені.
// Ідея: при відкриваючій дужці кладемо її у стек.
// При закриваючій — знімаємо верхню зі стека і перевіряємо, чи вона "та сама пара".
public class ValidParentheses {

    public static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        // стек відкритих дужок, які ще чекають на закриття

        Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');
        // закриваюча дужка -> відповідна їй відкриваюча

        for (char c : s.toCharArray()) {
            if (pairs.containsValue(c)) {
                stack.push(c);
                // це відкриваюча дужка -> кладемо в стек

            } else {
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) return false;
                // це закриваюча дужка:
                // якщо стек порожній (нема що закривати) АБО
                // верхівка стека не збігається з очікуваною парою — рядок невалідний
            }
        }
        return stack.isEmpty();
        // якщо в кінці стек порожній — усі дужки закриті; якщо ні — щось лишилось незакритим
    }

    public static void main(String[] args) {
        System.out.println(isValid("()[]{}"));  // true
        System.out.println(isValid("(]"));      // false
        System.out.println(isValid("{[]}"));    // true
    }
}
