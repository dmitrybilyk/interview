package com.conduct.interview._0_bytecode_and_compiler.jit_compiler;

public class JITExample {
    // Method that will be executed multiple times to trigger JIT compilation
    public static long computeFactorial(int n) {
        if (n == 1) {
            return 1;
        } else {
            return n * computeFactorial(n - 1);
        }
    }

    public static void main(String[] args) {
        // Warm-up phase (executing the code enough times to trigger JIT)
        System.out.println("Warming up the JIT compiler...");
        for (int i = 0; i < 10000; i++) {
            computeFactorial(20);
        }

        // Measure the time after the JIT has optimized the code
        System.out.println("Measuring performance after JIT optimization...");

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            computeFactorial(20);
        }
        long endTime = System.nanoTime();

        System.out.println("Execution time after JIT optimization: " + (endTime - startTime) + " nanoseconds.");
    }
}
