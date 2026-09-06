package com.conduct.interview._7_patterns.structural.adapter;

public class MyAdapter {
    public static void main(String[] args) {
        ReturnsString returnsString = new ReturnsString();
        IntegerAdapter integerAdapter = new IntegerAdapter(returnsString);
        someMethodToUseInt(integerAdapter.getValue());
    }

    private static void someMethodToUseInt(Integer returnsString) {
        System.out.println(returnsString);
    }
}

class ReturnsString {
    String getValue() {
        return "20";
    }
}

class IntegerAdapter{
    public IntegerAdapter(ReturnsString returnsString) {
        this.returnsString = returnsString;
    }

    private ReturnsString returnsString;

    Integer getValue() {
        return Integer.valueOf(returnsString.getValue());
    }
}