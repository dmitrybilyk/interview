package com.conduct.interview._1_bases.java8.stream;

/**
 * Created by dik81 on 12.01.19.
 */
public class SomeClassWithStaticCompareIntMethod {
    public static int compare2(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
    public int nonStaticCompare2(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
