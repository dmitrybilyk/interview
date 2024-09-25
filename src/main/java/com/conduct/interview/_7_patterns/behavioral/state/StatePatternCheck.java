package com.conduct.interview._7_patterns.behavioral.state;

public class StatePatternCheck {
  public static void main(String[] args) {
    Lamp lamp = new Lamp();
    lamp.turnBrighter();
    lamp.turnBrighter();
    // lamp.turnDarker();

    lamp.currentLight();
  }
}

class Lamp {
  LightState lightState;

  public Lamp() {
    lightState = new NoLightState(this);
  }

  public void turnBrighter() {
    lightState.turnBrighter();
  }

  public void turnDarker() {
    lightState.turnDarker();
  }

  public void changeState(LightState state) {
    this.lightState = state;
  }

  public void currentLight() {
    this.lightState.levelOfLight();
  }
}

abstract class LightState {
  Lamp lamp;

  public LightState(Lamp lamp) {
    this.lamp = lamp;
  }

  abstract void turnBrighter();

  abstract void turnDarker();

  abstract void levelOfLight();
}

class BrightLightState extends LightState {
  public BrightLightState(Lamp lamp) {
    super(lamp);
  }

  public void turnBrighter() {
    System.out.println("Top brightness already!");
  }

  public void turnDarker() {
    lamp.changeState(new AvarageLightState(lamp));
  }

  public void levelOfLight() {
    System.out.println("Very very bright light");
  }
}

class AvarageLightState extends LightState {
  public AvarageLightState(Lamp lamp) {
    super(lamp);
  }

  public void turnBrighter() {
    lamp.changeState(new BrightLightState(lamp));
  }

  public void turnDarker() {
    lamp.changeState(new DarkLightState(lamp));
  }

  public void levelOfLight() {
    System.out.println("Avarage light");
  }
}

class DarkLightState extends LightState {
  public DarkLightState(Lamp lamp) {
    super(lamp);
  }

  public void turnBrighter() {
    lamp.changeState(new AvarageLightState(lamp));
  }

  public void turnDarker() {
    lamp.changeState(new NoLightState(lamp));
  }

  public void levelOfLight() {
    System.out.println("Dark light");
  }
}

class NoLightState extends LightState {
  public NoLightState(Lamp lamp) {
    super(lamp);
  }

  public void turnBrighter() {
    lamp.changeState(new DarkLightState(lamp));
  }

  public void turnDarker() {
    System.out.println("already no light");
  }

  public void levelOfLight() {
    System.out.println("no light at all");
  }
}
