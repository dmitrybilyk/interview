package com.conduct.interview._7_patterns.creational.factory.simple_factory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SimpleFactory {
  public static void main(String[] args) {
    User user = SimpleUserFactory.createUser("good");
    System.out.println(user.getName());
  }
}

@Getter
@Setter
@NoArgsConstructor
class User {
  public String name;

  public User(String name) {
    this.name = name;
  }
}

@AllArgsConstructor
// @NoArgsConstructor
class GoodUser extends User {
  public GoodUser(String name) {
    this.name = name;
  }
}

@AllArgsConstructor
// @NoArgsConstructor
class BadUser extends User {
  public BadUser(String name) {
    this.name = name;
  }
}

class SimpleUserFactory {
  public static User createUser(String param) {
    if (param.equals("good")) {
      return new GoodUser(param);
    } else {
      return new BadUser(param);
    }
  }
}
