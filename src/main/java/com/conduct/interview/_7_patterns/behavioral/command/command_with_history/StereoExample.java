package com.conduct.interview._7_patterns.behavioral.command.command_with_history;

import lombok.Getter;
import lombok.Setter;

import java.util.Stack;

public class StereoExample {
    public static void main(String[] args) {
        Stereo stereo = new Stereo();

        Command playCommand = new PlayCommand(stereo);
        Command stopCommand = new StopCommand(stereo);
        Command increaseVolumeCommand = new IncreaseVolumeCommand(stereo);
        Command decreaseVolumeCommand = new DecreaseVolumeCommand(stereo);

        StereoInvoker stereoInvoker = new StereoInvoker();
        stereoInvoker.setCommand(playCommand);
        stereoInvoker.execute();
        stereoInvoker.undo();

        stereoInvoker.setCommand(stopCommand);
        stereoInvoker.execute();
        stereoInvoker.undo();

        stereoInvoker.setCommand(increaseVolumeCommand);
        stereoInvoker.execute();
        stereoInvoker.execute();
        stereoInvoker.execute();
        System.out.println("Volume is - " + stereo.getVolume());
        stereoInvoker.undo();
        System.out.println("Volume is - " + stereo.getVolume());

        stereoInvoker.setCommand(decreaseVolumeCommand);
        stereoInvoker.execute();
        System.out.println("Volume is - " + stereo.getVolume());
        stereoInvoker.undo();
        System.out.println("Volume is - " + stereo.getVolume());

        stereoInvoker.setCommand(playCommand);
        stereoInvoker.execute();
        stereoInvoker.setCommand(stopCommand);
        stereoInvoker.execute();
        stereoInvoker.setCommand(playCommand);
        stereoInvoker.execute();
        stereoInvoker.setCommand(playCommand);
        stereoInvoker.execute();
        stereoInvoker.setCommand(increaseVolumeCommand);
        stereoInvoker.execute();

        stereoInvoker.undo();
        stereoInvoker.undo();
        stereoInvoker.undo();
        stereoInvoker.undo();
        stereoInvoker.execute();

    }
}

//Receiver
@Getter
class Stereo {
    private int volume = 10;

    public void increaseVolume() {
        this.volume += 5;
    }

    public void decreaseVolume() {
        this.volume -= 5;
    }

    public void play() {
        System.out.println("Stereo is on");
    }

    public void stop() {
        System.out.println("Stereo is off");
    }
}

interface Command {
    void execute();
    void undo();
}

class PlayCommand implements Command {
    private final Stereo stereo;

    public PlayCommand(Stereo stereo) {
        this.stereo = stereo;
    }

    @Override
    public void execute() {
        stereo.play();
    }

    @Override
    public void undo() {
        stereo.stop();
    }
}

class StopCommand implements Command {
    private final Stereo stereo;

    public StopCommand(Stereo stereo) {
        this.stereo = stereo;
    }

    @Override
    public void execute() {
        stereo.stop();
    }

    @Override
    public void undo() {
        stereo.play();
    }
}

class IncreaseVolumeCommand implements Command {
    private final Stereo stereo;

    public IncreaseVolumeCommand(Stereo stereo) {
        this.stereo = stereo;
    }

    @Override
    public void execute() {
        stereo.increaseVolume();
    }

    @Override
    public void undo() {
        stereo.decreaseVolume();
    }
}

class DecreaseVolumeCommand implements Command {
    private final Stereo stereo;

    public DecreaseVolumeCommand(Stereo stereo) {
        this.stereo = stereo;
    }

    @Override
    public void execute() {
        stereo.decreaseVolume();
    }

    @Override
    public void undo() {
        stereo.increaseVolume();
    }
}

class StereoInvoker {
    @Setter
    private Command command;
    private final Stack<Command> commandsHistory = new Stack<>();

    public void execute() {
        command.execute();
        commandsHistory.add(command);
    }
    public void undo() {
        if (!commandsHistory.isEmpty()) {
            commandsHistory.pop().undo();
        } else {
            System.out.println("No commands to undo.");
        }
    }

}