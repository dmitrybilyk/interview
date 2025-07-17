package com.conduct.interview._20_algorythms_and_data_structures.algorithms;

// Advantages of Recursive Programming
// The advantages of recursive programs are as follows:
// Recursion provides a clean and simple way to write code.
// Some problems are inherently recursive like tree traversals, Tower of Hanoi, etc. For such
// problems, it is preferred to write recursive code.
// Disadvantages of Recursive Programming
// The disadvantages of recursive programs is as follows:
//
// The recursive program has greater space requirements than the iterative program as all functions
// will remain in the stack until the base case is reached.
// It also has greater time requirements because of function calls and returns overhead.

public class Recursion {
  private static final int distance = 1000;

  public static void main(String[] args) {
//    iterativeApproach();
//    recursionApproach(1);

    var factorialCalculator = new FactorialCalculator();
    System.out.println(factorialCalculator.factorial(5));

//    var fiboCalculator = new FiboCalculator();
//    System.out.println(fiboCalculator.fibo(5));
  }

  private static void iterativeApproach() {
    for (int i = 1; i <= Recursion.distance; i++) {
      System.out.println("step " + i);
    }
  }

//  public

  private static void recursionApproach(int step) {
    if (step <= distance) {
      System.out.println("step " + step);
      recursionApproach(step + 1);
    }
  }
}

class FactorialCalculator {
  public int factorial(int n) {
    if (n <= 1) {
      return 1;
    }
    return n * factorial(n - 1);
  }
}

class FiboCalculator {
  public int fibo(int n) {
    if (n <= 1) {
      return 1;
    }
    return fibo(n - 1) + fibo(n - 2);
  }
}
