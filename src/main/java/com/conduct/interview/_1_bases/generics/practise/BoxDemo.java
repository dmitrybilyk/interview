package com.conduct.interview._1_bases.generics.practise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoxDemo {

//  public static <U> void addBox(U u,
//      List<Box<U>> boxes) {
//    Box<U> box = new Box<>();
//    box.set(u);
//    boxes.add(box);
//  }
//
//  public static <U> void outputBoxes(List<Box<U>> boxes) {
//    int counter = 0;
//    for (Box<U> box: boxes) {
//      U boxContents = box.get();
//      System.out.println("Box #" + counter + " contains [" +
//             boxContents.toString() + "]");
//      counter++;
//    }
//  }

  public static void main(String[] args) {
    java.util.ArrayList<Box<Integer>> listOfIntegerBoxes =
      new java.util.ArrayList<>();
//    BoxDemo.<Integer>addBox(Integer.valueOf(10), listOfIntegerBoxes);
//    BoxDemo.addBox(Integer.valueOf(20), listOfIntegerBoxes);
//    BoxDemo.addBox(Integer.valueOf(30), listOfIntegerBoxes);
//    BoxDemo.outputBoxes(listOfIntegerBoxes);

    new MyClass<>("10");

    List<Double> list = Arrays.asList(1.0, 2.0, 3.0);
    System.out.println(ToUseList.sum(list));


    Node<String> node = new Node<>();
    Comparable<String> comp = node;



//    List<EvenNumber> le = new ArrayList<>();
//    List<? extends NaturalNumber> ln = le;
//    ln.add(new NaturalNumber(35));  // compile-time error
    String[] array = {"first", "second", "third", "fourth"};
    ForSwapPositionsInArray.swap(array, 2, 3);

    Arrays.stream(array).forEach(System.out::println);

//    List<Circle> circles = Arrays.asList();
    List<Shape> shapes = new ArrayList<>();
//    shapes = circles;

//    someMethod(shapes);

  }

//  static void <T> someMethod(List<Circle> shapes) {
//    shapes.add(new Circle());
////    for (Shape shape : shapes) {
////      System.out.println(shape);
////    }
//  }
  static void someMethod2(List<? extends Shape> shapes) {
//    Circle circle = shapes.get(0);
    for (Shape shape : shapes) {
      System.out.println(shape);
    }
  }
}

class Node<T> implements Comparable<T> {
  public int compareTo(T obj) {
    return 10;
    /* ... */ }
  // ...
}

class ShapeParent { /* ... */ }
class Shape extends ShapeParent { /* ... */ }
class Circle extends Shape { /* ... */ }

class Pair{

  public Pair(Object key, Object value) {
    this.key = key;
    this.value = value;
  }

  public Object getKey() { return key; }
  public Object getValue() { return value; }

  public void setKey(Object key)     { this.key = key; }
  public void setValue(Object value) { this.value = value; }

  private Object key;
  private Object value;
}

class ForSwapPositionsInArray {
  public static  <T> void swap(T[] array, int first, int second) {
    T temp = array[first];
    array[first] = array[second];
    array[second] = temp;
  }

  public static void print(List<? extends Number> list) {
    for (Number n : list)
      System.out.print((" " + n + n));
    System.out.println();
  }

  public static <T extends Comparable<T>> T findMax(List<T> list, int start, int end) {
    T max = list.get(start);
    for (int i = start; i <= end; i++) {
      if (list.get(i).compareTo(max) > 0) {
        max = list.get(i);
      }
    }
    return max;
  }

}


class Singleton<T> {
  private T instance;

  @SuppressWarnings("unchecked")
  public T getInstance(Class<T> clazz) throws Exception {
    if (instance == null) {
      instance = clazz.getDeclaredConstructor().newInstance();
    }
    return instance;
  }
}

//final class Algorithm {
//  public static <T> T max(T x, T y) {
////    return x > y ? x : y;
//  }
//}

class MyClass<X> {
  <T> MyClass(T t) {
    // ...
  }
}


class ToUseList {
  public static double sum(List<? extends Number> list) {
    double s = 0.0;
    for (Number n : list)
      s += n.doubleValue();
    return s;
  }
}


class WildcardError {

  void foo(List<?> i) {
//    i.set(0, i.get(0));
  }
}

class WildcardFixed {

  void foo(List<?> i) {
    fooHelper(i);
  }


  // Helper method created so that the wildcard can be captured
  // through type inference.
  private <T> void fooHelper(List<T> l) {
    l.set(0, l.get(0));
  }

}

class NaturalNumber {

  private int i;

  public NaturalNumber(int i) { this.i = i; }
  // ...
}

class EvenNumber extends NaturalNumber {

  public EvenNumber(int i) { super(i); }
  // ...
}
