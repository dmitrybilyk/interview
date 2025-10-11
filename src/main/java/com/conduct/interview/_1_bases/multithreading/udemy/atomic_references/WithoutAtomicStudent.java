package com.conduct.interview._1_bases.multithreading.udemy.atomic_references;

public class WithoutAtomicStudent {

    static class Student {
        String name;
        int score;

        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }

        void addScore(int value) {
            this.score += value;
        }
    }

    private static Student student = new Student("Alice", 0);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> incrementScore("T1"));
        Thread t2 = new Thread(() -> incrementScore("T2"));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final score (without atomic): " + student.score);
    }

    private static void incrementScore(String threadName) {
        for (int i = 0; i < 1000; i++) {
            student.addScore(1); // not atomic!
        }
    }
}
