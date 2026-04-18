package com.conduct.interview._1_bases.java8.streams.reduce;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ReduceTest {
    public static void main(String[] args) {
        List<String> list = List.of("First", "Second");
        System.out.println(list.stream()
                .reduce("start", (s, s2) -> s + s2));
//                .reduce("start", String::concat));

        int sum = List.of(1, 2, 3, 4)
                .stream()
                .reduce(0, (a, b) -> a + b);
        System.out.println(sum);

        int sum2 = List.of(1, 2, 3, 4)
                .stream()
                .mapToInt(Integer::valueOf)
                .sum();
        System.out.println(sum2);

        int max = List.of(1, 2, 3, 4)
                .stream()
                .max(Integer::compareTo).get();
        System.out.println(max);


        List<Order> orders = List.of(
                new Order(100),
                new Order(200),
                new Order(50)
        );

        Order total = orders.stream()
                .reduce(new Order(), (acc, order) -> acc.combine(order));

        System.out.println(total); // Order{totalPrice=350}

        int total2 = orders.stream()
                .mapToInt(o -> o.totalPrice)
                .sum();

        System.out.println(total2);

        Order total3 = orders.stream()
                .collect(
                        Order::new,
                        (acc, order) -> acc.totalPrice += order.totalPrice,
                        (o1, o2) -> o1.totalPrice += o2.totalPrice
                );

    }
}


class Order {
    int totalPrice;

    Order() {
        this.totalPrice = 0;
    }

    Order(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    Order combine(Order other) {
        return new Order(this.totalPrice + other.totalPrice);
    }

    @Override
    public String toString() {
        return "Order{totalPrice=" + totalPrice + '}';
    }
}
