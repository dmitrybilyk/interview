package com.conduct.interview._1_bases.java8.stream;

import java.util.*;

/** Created by dik81 on 12.01.19. */
public class Test {
  public void test() {
    ArrayList<Integer> numbers = new ArrayList<Integer>();
    numbers.addAll(Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9}));

    //        Optional<Integer> min = numbers.stream().min(Test.this::nonStaticCompare2);
    //        Optional<Integer> max =
    // numbers.stream().max(SomeClassWithStaticCompareIntMethod::nonStaticCompare2);
    //        System.out.println(min.get());  // 1
    //        System.out.println(max.get());  // 9

  }
}
