package com.conduct.interview._7_patterns.behavioral.state;

public class StatePatternCheckTV {
  public static void main(String[] args) {
    TvSet tvSet = new TvSet();
    tvSet.changeState(new OnState());
    tvSet.showing();

    tvSet.changeState(new OffState());
    tvSet.showing();
  }
}

class TvSet {
  StateClass state;

  public void changeState(StateClass state) {
    this.state = state;
  }

  public void showing() {
    state.action();
  }
}

abstract class StateClass {
  abstract void action();
}

class OnState extends StateClass {
  public void action() {
    System.out.println("Tv is showing");
  }
}

class OffState extends StateClass {
  public void action() {
    System.out.println("Tv is off");
  }
}
