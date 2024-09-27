package com.conduct.interview._7_patterns.behavioral.iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class IteratorCheck {
    public static void main(String[] args) {
        MyCollection<Student> studentMyCollection = new MyCollectionImpl<>();
        studentMyCollection.add(new Student("Dmytro", 43));
        studentMyCollection.add(new Student("Ruslan", 47));
        studentMyCollection.add(new Student("Mykyta", 16));

        MyIterator<Student> myIterator = studentMyCollection.simpleIterator();

        while (myIterator.hasNext()) {
            System.out.println(myIterator.next());
        }


        MyCollection<Human> humanMyCollection = new MyCollectionImpl<>();
        humanMyCollection.add(new Human("Dmytro", 43));
        humanMyCollection.add(new Human("Ruslan", 47));
        humanMyCollection.add(new Human("Mykyta", 16));
        humanMyCollection.add(new Human("Mykyta2", 17));
        humanMyCollection.add(new Human("Mykyta3", 18));
        humanMyCollection.add(new Human("Mykyta4", 96));

        MyIterator<Human> myHumanIterator = humanMyCollection.evenIterator();

        while (myHumanIterator.hasNext()) {
            System.out.println(myHumanIterator.next());
        }

        for (Human s : humanMyCollection) {
            System.out.println("foreach - " + s);
        }

        humanMyCollection.forEach(human -> System.out.println("fff foreacheach " + human));

    }
}

interface MyCollection<T> extends Iterable<T> {
    MyIterator<T> simpleIterator();
    MyIterator<T> evenIterator();
    void add(T t);
}

interface MyIterator<T> {
    T next();
    boolean hasNext();
}

class MyIteratorImpl<T> implements MyIterator<T>, Iterator<T> {
    private final List<T> items;
    private int position;

    MyIteratorImpl(List<T> items) {
        this.items = items;
    }
    @Override
    public T next() {
        return position < items.size() ? items.get(position++) : null;
    }

    @Override
    public boolean hasNext() {
        return position < items.size();
    }
}

class MyEvenIteratorImpl<T> implements MyIterator<T> {
    private final List<T> items;
    private int position;

    MyEvenIteratorImpl(List<T> items) {
        this.items = items;
    }
    @Override
    public T next() {
        if (position < items.size()) {
            int newPosition = position++;
            if (newPosition % 2 == 0) {
                return items.get(position++);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return position < items.size();
    }
}

@NoArgsConstructor
class MyCollectionImpl<T> implements MyCollection<T>, Iterable<T> {
    private List<T> items = new ArrayList<>();
    MyCollectionImpl(List<T> items) {
        this.items = items;
    }

    @Override
    public MyIterator<T> simpleIterator() {
        return new MyIteratorImpl<>(items);
    }

    @Override
    public MyIterator<T> evenIterator() {
        return new MyEvenIteratorImpl<>(items);
    }

    public void add(T t) {
        items.add(t);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new MyIteratorImpl<>(items);
    }

    // forEach method using the internal list
    @Override
    public void forEach(Consumer<? super T> action) {
        for (T item : items) {
            action.accept(item);  // Apply the action to each item
        }
    }

    // spliterator method using the internal list
    @Override
    public Spliterator<T> spliterator() {
        return items.spliterator();  // Return the spliterator of the internal list
    }
}

@Getter
@Setter
@AllArgsConstructor
@ToString
class Student {
    private String name;
    private int age;
}


@Getter
@Setter
@AllArgsConstructor
@ToString
class Human {
    private String humanName;
    private int humanAge;
}