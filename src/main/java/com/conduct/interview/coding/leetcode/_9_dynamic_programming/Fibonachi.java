package com.conduct.interview.coding.leetcode._9_dynamic_programming;

import java.util.stream.Stream;

// LC 509 — Fibonacci Number
// Той самий Фібоначчі, що й у ClimbingStairs, але у "функціональному" стилі через Stream.
// Час O(n), Пам'ять O(1) (не рахуючи самого стріму)
public class Fibonachi {
    public static void main(String[] args) {
        printFibonici(5);
    }

    private static void printFibonici(int n) {
        Stream.iterate(new long[]{0, 1}, fib -> new long[]{fib[1], fib[0] + fib[1]})
                // кожен наступний елемент стріму — пара [fib[1], fib[0]+fib[1]]:
                // "зсуваємо" пару вперед так само, як a,b у звичайному циклічному Фібоначчі

                .limit(n)
                // беремо тільки перші n чисел послідовності

                .map(fib -> fib[0])
                // з кожної пари нам потрібне лише перше число — це і є fibonacci(i)

                .forEach(System.out::println);
    }
}
