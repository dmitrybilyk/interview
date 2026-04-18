package com.conduct.interview._1_bases.java8.streams.reduce.correct_collect;

import java.util.List;

class Order {
    int total;

    Order() {
        this.total = 0;
    }
}

public class CollectFix {
    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(), new Order(), new Order(), new Order()
        );

        // setting values for demo
        orders.get(0).total = 100;
        orders.get(1).total = 200;
        orders.get(2).total = 300;
        orders.get(3).total = 400;

        Order result = orders
                .parallelStream()
                .collect(
                        Order::new,
                        (acc, order) -> acc.total += order.total,
                        (a, b) -> a.total += b.total
                );

        System.out.println(result.total);
    }
}