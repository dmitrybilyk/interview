package com.conduct.interview._7_patterns.structural.composite;

import java.util.ArrayList;
import java.util.List;

public class CompositeCheck2 {
    public static void main(String[] args) {
        Container container = new Container();
        Container container1 = new Container();
        container1.capacity = 30;
        Container container2 = new Container();
        container2.capacity = 20;
        Box box = new Box();
        box.capacity = 30;
        Box box1 = new Box();
        box1.capacity = 20;
        container.addContainer(container2);
        container.addContainer(container1);
        container1.addContainer(box1);
        container1.addContainer(box);

        System.out.println(container.calculateCapacity());
    }
}

interface Component {
    int calculateCapacity();
}

class Container {
    private List<Container> containerList = new ArrayList<>();
    protected int capacity;

    public void addContainer(Container container) {
        containerList.add(container);
    }

    int calculateCapacity() {
        int result = 0;
        for (Container container : containerList) {
            result += container.capacity;
        }
        return capacity + result;
    }
}

class Box extends Container {
    @Override
    int calculateCapacity() {
        return capacity;
    }
}
