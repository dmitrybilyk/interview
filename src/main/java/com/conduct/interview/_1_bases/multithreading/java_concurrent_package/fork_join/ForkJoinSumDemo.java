package com.conduct.interview._1_bases.multithreading.java_concurrent_package.fork_join;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Divide and conquer: split the array in half recursively until a chunk is small enough
 * (the threshold), sum each chunk in parallel, then combine the results.
 */
public class ForkJoinSumDemo {

    public static void main(String[] args) {
        int[] numbers = new int[10_000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }

        long sum = ForkJoinPool.commonPool().invoke(new SumTask(numbers, 0, numbers.length));
        System.out.println("Sum: " + sum);
    }

    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 1000;
        private final int[] numbers;
        private final int from;
        private final int to;

        SumTask(int[] numbers, int from, int to) {
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Long compute() {
            if (to - from <= THRESHOLD) {
                long sum = 0;
                for (int i = from; i < to; i++) {
                    sum += numbers[i];
                }
                return sum;
            }

            int mid = from + (to - from) / 2;
            SumTask left = new SumTask(numbers, from, mid);
            SumTask right = new SumTask(numbers, mid, to);

            left.fork();                 // run left half asynchronously
            long rightResult = right.compute(); // compute right half on this thread
            long leftResult = left.join();       // wait for left half's result

            return leftResult + rightResult;
        }
    }
}
