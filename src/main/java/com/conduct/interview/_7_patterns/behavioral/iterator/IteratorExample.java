package com.conduct.interview._7_patterns.behavioral.iterator;

// Iterator Interface with Generics
interface Iterator<T> {
    boolean hasNext();
    T next();
}

// Collection Interface with Generics
interface Collection<T> {
    Iterator<T> createIterator();
}

// Concrete Iterator with Generics
class IteratorImpl<T> implements Iterator<T> {
    private T[] items;
    private int position = 0;

    public IteratorImpl(T[] items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return position < items.length;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            return items[position++];
        }
        return null;
    }
}

// Concrete Collection with Generics
class CollectionImpl<T> implements Collection<T> {
    private T[] items;

    public CollectionImpl(T[] items) {
        this.items = items;
    }

    @Override
    public Iterator<T> createIterator() {
        return new IteratorImpl<>(items);
    }
}

// Usage
public class IteratorExample {
    public static void main(String[] args) {
        // Using the generic Collection and Iterator for Strings
        String[] stringItems = {"A", "B", "C", "D"};
        Collection<String> stringCollection = new CollectionImpl<>(stringItems);
        Iterator<String> stringIterator = stringCollection.createIterator();

        while (stringIterator.hasNext()) {
            System.out.println(stringIterator.next());
        }

        // Using the generic Collection and Iterator for Integers
        Integer[] intItems = {1, 2, 3, 4};
        Collection<Integer> intCollection = new CollectionImpl<>(intItems);
        Iterator<Integer> intIterator = intCollection.createIterator();

        while (intIterator.hasNext()) {
            System.out.println(intIterator.next());
        }
    }
}
