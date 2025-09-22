package com.conduct.interview._1_bases.multithreading.udemy.interrupt_thread;

public class LatencyOptimizationWithThreads {
    public static void main(String[] args) throws InterruptedException {
        int size = 20_000_000;
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i % 100;
        }

        // 1️⃣ Single-threaded version
        long start = System.currentTimeMillis();
        long sumSingle = computeSum(numbers, 0, size);
        long end = System.currentTimeMillis();
        System.out.println("Single-thread sum = " + sumSingle +
                           " in " + (end - start) + " ms");

        // 2️⃣ Multi-threaded version with 4 threads
        int numThreads = 4;
        Worker[] workers = new Worker[numThreads];
        Thread[] threads = new Thread[numThreads];
        int chunk = size / numThreads;

        start = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            int from = i * chunk;
            int to = (i == numThreads - 1) ? size : from + chunk;
            workers[i] = new Worker(numbers, from, to);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }

        long sumMulti = 0;
        for (int i = 0; i < numThreads; i++) {
            threads[i].join(); // wait for worker to finish
            sumMulti += workers[i].getResult();
        }
        end = System.currentTimeMillis();

        System.out.println("Multi-thread sum = " + sumMulti +
                           " in " + (end - start) + " ms");
    }

    // Heavy computation
    private static long computeSum(int[] arr, int start, int end) {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += Math.sqrt(arr[i]) * Math.sqrt(arr[i]); // simulate CPU work
        }
        return sum;
    }

    // Worker that runs in its own thread
    static class Worker implements Runnable {
        private final int[] arr;
        private final int start;
        private final int end;
        private long result = 0;

        Worker(int[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            result = computeSum(arr, start, end);
        }

        public long getResult() {
            return result;
        }
    }
}
