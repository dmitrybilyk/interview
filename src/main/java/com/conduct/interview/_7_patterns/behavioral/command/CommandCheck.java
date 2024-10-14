package com.conduct.interview._7_patterns.behavioral.command;

import lombok.AllArgsConstructor;

public class CommandCheck {
    public static void main(String[] args) {
        Command command = new CommandImpl(new CommandReceiver());
        CommandInvoker commandInvoker = new CommandInvoker(command);
        commandInvoker.createRequest();

    }
}

@AllArgsConstructor
class CommandInvoker {
    private Command command;
    void createRequest() {
        command.execute();
    }
}

interface Command {
    void execute();
}

@AllArgsConstructor
class CommandImpl implements Command {
    private CommandReceiver receiver;
    @Override
    public void execute() {
        receiver.actualExecute();
    }
}

class CommandReceiver {
    public void actualExecute() {
        System.out.println("Handling the command");
    }
}