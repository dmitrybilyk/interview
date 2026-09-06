package com.conduct.interview._7_patterns.behavioral.command;

public class Check {
    public static void main(String[] args) {
//        Invoker invoker = new Invoker(() -> {
//            System.out.println("Invoker invoked, the command is in action");
//        });

        CommandInterface command = new StandUpCommand(new Receiver());
        CommandInterface command2 = new SitDownCommand(new Receiver());
        Invoker invoker = new Invoker(command);
        invoker.invoke();

//        invoker.
    }
}

class Invoker {
    public Invoker(CommandInterface command) {
        this.command = command;
    }

    private CommandInterface command;

    public void invoke() {
        command.execute();
    }
}

interface CommandInterface {
    void execute();
//    void execute2();
}

class StandUpCommand implements CommandInterface {
    private Receiver receiver;

    public StandUpCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.standUp();
        System.out.println("StandUpCommand");
    }
}
class SitDownCommand implements CommandInterface {
    private Receiver receiver;

    public SitDownCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    @Override
    public void execute() {
        receiver.sitDown();
        System.out.println("SitDownCommand");
    }
}

class Receiver {

    public void standUp() {
        System.out.println("StandUpCommand in receiver");
    }
    public void sitDown() {
        System.out.println("SitDownCommand in receiver");
    }
}