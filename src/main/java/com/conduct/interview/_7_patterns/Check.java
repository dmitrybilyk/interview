package com.conduct.interview._7_patterns;

public class Check {
    public static void main(String[] args) {
        Receiver receiver = new ReceiverImpl();
        Command command = () -> {
            receiver.receive();
            System.out.println("ssssssssssssssssssssssssssssssss");
        };
        Invoker invoker = new InvokerImpl(command);
        invoker.initiateExecution();
    }
}

interface Invoker {
    void initiateExecution();
}

class InvokerImpl implements Invoker {
    private Command command;

    public InvokerImpl(Command command) {
        this.command = command;
    }

    @Override
    public void initiateExecution() {
        command.execute();
    }
}

interface Command {
    void execute();
}

class CommandImpl implements Command {
    public CommandImpl(Receiver receiver) {
        this.receiver = receiver;
    }

    private Receiver receiver;
    @Override
    public void execute() {
        receiver.receive();
    }
}


interface Receiver {
    void receive();
}

class ReceiverImpl implements Receiver {

    @Override
    public void receive() {
        System.out.println("Receiver received");
    }
}