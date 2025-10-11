package com.conduct.interview._1_bases.multithreading.udemy.atomic_references;

import java.util.concurrent.atomic.AtomicReference;

public class WithAtomicStudent {

    static class Student {
        String name;
        int score;

        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }

        Student copyWithAddedScore(int delta) {
            return new Student(this.name, this.score + delta);
        }

        @Override
        public String toString() {
            return "Student{name='" + name + "', score=" + score + "}";
        }
    }

    private static AtomicReference<Student> studentRef =
            new AtomicReference<>(new Student("Alice", 0));

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> incrementScore("T1"));
        Thread t2 = new Thread(() -> incrementScore("T2"));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final student (with atomic): " + studentRef.get());
    }

    private static void incrementScore(String threadName) {
        for (int i = 0; i < 1000; i++) {
            studentRef.updateAndGet(s -> s.copyWithAddedScore(1));
        }
    }
}
