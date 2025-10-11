package com.conduct.interview._7_patterns.behavioral.command;

import java.util.ArrayList;
import java.util.List;

public class CommandCheck2 {
    public static void main(String[] args) throws InterruptedException {
        LightSystem lightSystem = new LightSystem();
        SmartHomeCommand turnOnTheLightCommand = new TurnOnTheLightCommand(lightSystem);
        SmartHomeCommand turnOffTheLightCommand = new TurnOffTheLightCommand(lightSystem);
        RemoteControl remoteControl = new RemoteControl(turnOnTheLightCommand);
        remoteControl.invoke();
        remoteControl.setCommand(turnOffTheLightCommand);
        remoteControl.invoke();
        remoteControl.undo();
        remoteControl.undo();
        remoteControl.undo();
        remoteControl.undo();

        TemperatureSystem temperatureSystem = new TemperatureSystem();
        SmartHomeCommand increaseTemperatureCommand = new IncreaseTemperatureCommand(temperatureSystem);
        increaseTemperatureCommand.execute();
        increaseTemperatureCommand.execute();
        increaseTemperatureCommand.execute();
        increaseTemperatureCommand.undo();

        MacroCommand macroCommand = new MacroCommand();
        macroCommand.addCommand(increaseTemperatureCommand);
        macroCommand.addCommand(turnOnTheLightCommand);
        macroCommand.execute();

        SmartHomeExecuteCommand resetTemperatureToZero = temperatureSystem::setToZero;
        SmartHomeExecuteCommand turnOnTheLight = lightSystem::turnOnTheLight;

        SmartHomeExecuteCommand macroSmartCommand = () -> {
            resetTemperatureToZero.execute();
            turnOnTheLight.execute();
        };

        Thread.sleep(4000);
        macroSmartCommand.execute();
    }
}

interface SmartHomeExecuteCommand {
    void execute();
}

interface SmartHomeCommand {
    void execute();
    void undo();
}

class RemoteControl {
    private SmartHomeCommand command;

    public RemoteControl(SmartHomeCommand command) {
        this.command = command;
    }

    public void setCommand(SmartHomeCommand command) {
        this.command = command;
    }

    public void invoke() {
        command.execute();
    }

    public void undo() {
        command.undo();
    }
}

class TurnOnTheLightCommand implements SmartHomeCommand {
    private LightSystem lightSystem;

    public TurnOnTheLightCommand(LightSystem lightSystem) {
        this.lightSystem = lightSystem;
    }

    @Override
    public void execute() {
        lightSystem.turnOnTheLight();
    }

    @Override
    public void undo() {
        lightSystem.turnOffTheLight();
    }
}

class TurnOffTheLightCommand implements SmartHomeCommand {
    private LightSystem lightSystem;

    public TurnOffTheLightCommand(LightSystem lightSystem) {
        this.lightSystem = lightSystem;
    }
    @Override
    public void execute() {
        lightSystem.turnOffTheLight();
    }

    @Override
    public void undo() {
        lightSystem.turnOnTheLight();
    }
}

class LightSystem {
    void turnOnTheLight() {
        System.out.println("Light is on");
    }
    void turnOffTheLight() {
        System.out.println("Light is off");
    }
}

class IncreaseTemperatureCommand implements SmartHomeCommand {
    private TemperatureSystem temperatureSystem;

    public IncreaseTemperatureCommand(TemperatureSystem temperatureSystem) {
        this.temperatureSystem = temperatureSystem;
    }

    @Override
    public void execute() {
        temperatureSystem.increaseTemperature(10);
    }

    @Override
    public void undo() {
        temperatureSystem.decreaseTemperature(10);
    }
}

class DecreaseTemperatureCommand implements SmartHomeCommand {
    private TemperatureSystem temperatureSystem;

    public DecreaseTemperatureCommand(TemperatureSystem temperatureSystem) {
        this.temperatureSystem = temperatureSystem;
    }

    @Override
    public void execute() {
        temperatureSystem.decreaseTemperature(10);
    }

    @Override
    public void undo() {
        temperatureSystem.increaseTemperature(10);
    }
}

class TemperatureSystem {
    private int currentTemperature = 0;
    void increaseTemperature(int value) {
        currentTemperature += value;
        System.out.println("Current temperature is - " + currentTemperature);
    }
    void decreaseTemperature(int value) {
        currentTemperature -= value;
        System.out.println("Current temperature is - " + currentTemperature);
    }

    void setToZero() {
        this.currentTemperature = 0;
        System.out.println("current is " + this.currentTemperature);
    }
}

class MacroCommand implements SmartHomeCommand {
    private List<SmartHomeCommand> commandList = new ArrayList<>();

    public void addCommand(SmartHomeCommand command) {
        commandList.add(command);
    }

    @Override
    public void execute() {
        commandList.forEach(SmartHomeCommand::execute);
    }

    @Override
    public void undo() {
        for (int i = commandList.size() - 1; i >= 0; i--) {
            commandList.get(i).undo();
        }
    }
}