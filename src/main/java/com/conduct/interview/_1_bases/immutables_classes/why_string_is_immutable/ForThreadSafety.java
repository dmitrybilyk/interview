package com.conduct.interview._1_bases.immutables_classes.why_string_is_immutable;

public class ForThreadSafety {
    public static void main(String[] args) {
        // Shared immutable String
        String message = "Hello, world!";
        
        // Create two threads that share the same String object
        Thread thread1 = new Thread(new MessagePrinter(message));
        Thread thread2 = new Thread(new MessagePrinter(message));

        thread1.start();
        thread2.start();
    }
}

class MessagePrinter implements Runnable {
    private String message;

    public MessagePrinter(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        // Print the message multiple times in a loop
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + message);
        }
    }
}
