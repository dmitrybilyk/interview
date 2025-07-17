package com.conduct.interview._1_bases.generics.practise;

public class Oracle {
    public static void main(String[] args) {
//        Box<Number> numberBox = new Box<>(10);
//        Box<Integer> integerBox = new Box<>(3);
//        numberBox = integerBox;
    }

}

class Box<T> {
    private T value;
    public Box(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}