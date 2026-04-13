package com.conduct.interview._1_bases.multithreading.common_issues.fork_join_pool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSum extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000; // Smallest unit of work
    private final long[] array;
    private final int start;
    private final int end;

    public ForkJoinSum(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        
        // 1. Execute sequentially if the task is small enough
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }

        // 2. Fork: Split the task into two
        int split = start + length / 2;
        ForkJoinSum leftTask = new ForkJoinSum(array, start, split);
        ForkJoinSum rightTask = new ForkJoinSum(array, split, end);

        leftTask.fork(); // Push left task to the queue (asynchronous)
        
        // 3. Join: Compute the right part and merge with the left result
        Long rightResult = rightTask.compute(); 
        Long leftResult = leftTask.join(); 

        return leftResult + rightResult;
    }

    public static void main(String[] args) {
        long[] data = new long[100_000];
        for (int i = 0; i < data.length; i++) data[i] = i;

        try (ForkJoinPool pool = new ForkJoinPool()) {
            long total = pool.invoke(new ForkJoinSum(data, 0, data.length));
            System.out.println("Total Sum: " + total);
        }
    }
}