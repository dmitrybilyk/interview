package com.conduct.interview.coding.leetcode._4_stack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

// LC 20 — Valid Parentheses
// Pattern: Stack — push open brackets, pop and check on close
// Time O(n), Space O(n)

// Задача: перевірити, чи дужки в рядку правильно збалансовані/вкладені.
// Ідея: при відкриваючій дужці кладемо її у стек.
// При закриваючій — знімаємо верхню зі стека і перевіряємо, чи вона "та сама пара".
public class ValidParenthesesCheck {

    public static boolean isValid(String s) {
        Deque<Character> characters = new ArrayDeque<>();

        Map<Character, Character> map = new HashMap<>();
        map.put(')', '(');
        map.put(']', '[');
        map.put('}', '{');

        for (char c: s.toCharArray()) {
            if (map.containsValue(c)) {
                characters.push(c);
            } else if (map.containsKey(c) && (characters.isEmpty() || characters.pop() != map.get(c))) {
                return false;
            }
        }
        return characters.isEmpty();

    }

    public static void main(String[] args) {
        System.out.println(isValid("(gfgfgf)[ggfgffg]{}"));  // true
        System.out.println(isValid("(]"));      // false
        System.out.println(isValid("{[]}"));    // true
    }
}
