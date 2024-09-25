package com.conduct.interview._7_patterns.behavioral.memento;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class MementoCheck2 {
  public static void main(String[] args) {
    Human human = new Human("first", 30);
    HumanMementoCareTaker humanMementoCareTaker = new HumanMementoCareTaker();
    humanMementoCareTaker.saveHuman(human);
    human.setName("second");
    human.setAge(40);
    humanMementoCareTaker.saveHuman(human);
    human.setName("third");
    human.setAge(50);
    humanMementoCareTaker.saveHuman(human);
    human.setName("fourth");
    human.setAge(60);
    humanMementoCareTaker.saveHuman(human);
    humanMementoCareTaker.restoreHuman(human);
    humanMementoCareTaker.restoreHuman(human);
    humanMementoCareTaker.restoreHuman(human);
    humanMementoCareTaker.restoreHuman(human);
    System.out.println(human);
  }
}

// @Getter
@Setter
@ToString
@AllArgsConstructor
class Human {
  private String name;
  private int age;

  //    @Getter
  //    @Setter
  @ToString
  @AllArgsConstructor
  private class HumanMemento {
    private String name;
    private int age;
  }

  public HumanMemento saveHuman() {
    return new HumanMemento(this.name, this.age);
  }

  public void restoreHuman(Object object) {
    HumanMemento humanMemento = (HumanMemento) object;
    this.name = humanMemento.name;
    this.age = humanMemento.age;
  }
}

class HumanMementoCareTaker {
  private List<Object> mementos = new ArrayList<>();

  public void saveHuman(Human human) {
    this.mementos.add(human.saveHuman());
  }

  public void restoreHuman(Human human) {
    human.restoreHuman(mementos.get(mementos.size() - 1));
    this.mementos.remove(mementos.size() - 1);
  }
}
