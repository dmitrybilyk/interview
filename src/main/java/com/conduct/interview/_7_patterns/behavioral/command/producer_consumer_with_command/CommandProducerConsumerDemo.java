import java.util.LinkedList;
import java.util.Queue;

// Receiver
class Light {
    public void turnOn() {
        System.out.println("Light is ON");
    }

    public void turnOff() {
        System.out.println("Light is OFF");
    }
}

// Command Interface
interface Command {
    void execute();
    void undo();
}

// Concrete Command Classes
class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOn();
    }

    @Override
    public void undo() {
        light.turnOff();
    }
}

class LightOffCommand implements Command {
    private Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOff();
    }

    @Override
    public void undo() {
        light.turnOn();
    }
}

// Command Queue (Buffer)
class CommandQueue {
    private final Queue<Command> queue = new LinkedList<>();
    private boolean isProducing = true; // Flag to indicate if producers are active

    public synchronized void enqueue(Command command) {
        queue.offer(command);
        notify(); // Notify consumers that a new command is available
    }

    public synchronized Command dequeue() throws InterruptedException {
        while (queue.isEmpty() && isProducing) {
            wait(); // Wait until a command is available
        }
        return queue.poll(); // Return null if not producing and queue is empty
    }

    public synchronized void setProducing(boolean producing) {
        this.isProducing = producing;
        notifyAll(); // Notify consumers to check the queue
    }
}

// Producer Class
class CommandProducer {
    private final CommandQueue commandQueue;

    public CommandProducer(CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    public void produce(Command command) {
        commandQueue.enqueue(command); // Produce a command and add to the queue
    }
}

// Consumer Class
class CommandConsumer implements Runnable {
    private final CommandQueue commandQueue;

    public CommandConsumer(CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Command command = commandQueue.dequeue(); // Consume command
                if (command == null) {
                    break; // Exit if no command and not producing
                }
                command.execute(); // Execute the command
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }
}

// Main Class
public class CommandProducerConsumerDemo {
    public static void main(String[] args) {
        CommandQueue commandQueue = new CommandQueue();
        CommandProducer producer = new CommandProducer(commandQueue);
        CommandConsumer consumer = new CommandConsumer(commandQueue);

        // Start the consumer in a new thread
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        Light light = new Light();
        Command lightOn = new LightOnCommand(light);
        Command lightOff = new LightOffCommand(light);

        // Produce commands
        producer.produce(lightOn);
        producer.produce(lightOff);
        producer.produce(lightOn);
        producer.produce(lightOff);

        // Indicate that no more commands will be produced
        commandQueue.setProducing(false); // Stop the producer

        // Notify the consumer to check the queue and exit
        try {
            consumerThread.join(); // Wait for the consumer to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All commands have been executed.");
    }
}
