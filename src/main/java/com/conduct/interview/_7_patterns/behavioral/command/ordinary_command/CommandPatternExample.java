package com.conduct.interview._7_patterns.behavioral.command.ordinary_command;

import lombok.AllArgsConstructor;
import lombok.Setter;

// Command Interface
interface Command {
  void execute();
}

@AllArgsConstructor
class LightOnCommandImpl implements Command {
  private Light light;
  @Override
  public void execute() {
    light.turnOn();
  }
}

@AllArgsConstructor
class LightOffCommandImpl implements Command {
  private Light light;
  @Override
  public void execute() {
    light.turnOff();
  }
}

// Receiver
class Light {
  public void turnOn() {
    System.out.println("Light is ON");
  }

  public void turnOff() {
    System.out.println("Light is OFF");
  }
}

// Invoker
@Setter
class RemoteControl {
  private Command command;

  public void pressButton() {
    command.execute();
  }
}

// Client
public class CommandPatternExample {
  public static void main(String[] args) {
    Light light = new Light();

    Command turnOn = new LightOnCommandImpl(light);
    Command turnOff = new LightOffCommandImpl(light);

    RemoteControl remote = new RemoteControl();

    // Turn the light ON
    remote.setCommand(turnOn);
    remote.pressButton();

    // Turn the light OFF
    remote.setCommand(turnOff);
    remote.pressButton();
  }
}
