package com.conduct.interview.practise.spring.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoodBean {
//    @Value(value = "good string value")
    private String name;
//    @Value(value = "55")
    private int age;
}
