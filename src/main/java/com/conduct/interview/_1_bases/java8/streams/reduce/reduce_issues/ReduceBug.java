package com.conduct.interview._1_bases.java8.streams.reduce.reduce_issues;

import java.util.List;

class Order {
    int total;

    Order(int total) {
        this.total = total;
    }

    Order add(Order other) {
        this.total += other.total;
        return this;
    }
}

public class ReduceBug {
    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(100),
                new Order(200),
                new Order(300),
                new Order(400)
        );

        Order result = orders
//                .parallelStream()
                .stream()
                .reduce(new Order(0), (acc, order) -> {
                    acc.total += order.total;   // ❌ mutation
                    return acc;
                });

        System.out.println(result.total);
    }
}