package com.conduct.interview._7_patterns.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

public class FlyWeightPattern {
  public static void main(String[] args) {
    HumanFlyweightFactory humanFlyweightFactory = new HumanFlyweightFactory();
    Human human = humanFlyweightFactory.getHumanWithSomeRace("nigger");
    human.setName("some nigger 1");
    System.out.println(human);
    Human human2 = humanFlyweightFactory.getHumanWithSomeRace("nigger");
    human2.setName("some nigger 2");
    System.out.println(human2);
  }
}

class Human {
  private String name;
  private String race;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRace() {
    return race;
  }

  public void setRace(String race) {
    this.race = race;
  }

  @Override
  public String toString() {
    return name + " " + race;
  }
}

class HumanFlyweightFactory {
  private Map<String, Human> humanMap = new HashMap<>();

  public Human getHumanWithSomeRace(String race) {
    Human human = humanMap.get(race);
    if (human == null) {
      human = new Human();
      human.setRace(race);
      humanMap.put(human.getRace(), human);
    }
    return human;
  }
}
