package com.conduct.interview._1_bases.java8.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Reduce {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        Optional<Integer> result = numbers
                .stream()
                .reduce((subtotal, element) -> subtotal + element + 4);
        result.ifPresent(System.out::println);


        List<User> users = Arrays.asList(new User("Dmytro", 33), new User("Mykyta", 16));
        System.out.println(users.stream().reduce(0, (integer, user) -> integer + user.getAge(), Integer::sum));
    }




}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class User {
    private String name;
    private int age;
}
