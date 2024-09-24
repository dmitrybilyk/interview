package com.conduct.interview._7_patterns.behavioral.command.command_with_lambda;

import lombok.Setter;

// Command Interface
interface Command {
    void execute();
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
        
        Command turnOn = light::turnOn;
        Command turnOff = light::turnOff;
        
        RemoteControl remote = new RemoteControl();

        // Turn the light ON
        remote.setCommand(turnOn);
        remote.pressButton();

        // Turn the light OFF
        remote.setCommand(turnOff);
        remote.pressButton();
    }
}
