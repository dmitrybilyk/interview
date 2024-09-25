package com.conduct.interview._7_patterns.behavioral.mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class MediatorCheck2 {
  public static void main(String[] args) {
    WorkerMediator mediator = new DoSomeJob2MediatorImpl();
    Worker worker = new WorkerImpl("Worker1", mediator);
    Worker worker2 = new WorkerImpl("Worker2", mediator);
    Worker worker3 = new WorkerImpl("Worker3", mediator);
    mediator.addWorker(worker);
    mediator.addWorker(worker2);
    mediator.addWorker(worker3);
    worker.sendMessage("Message from worker");
  }
}

interface Worker {
  public void sendMessage(String message);

  public void receiveMessage(String message);
}

@Getter
@Setter
@AllArgsConstructor
class WorkerImpl implements Worker {
  private String name;
  private WorkerMediator workerMediator;

  @Override
  public void sendMessage(String message) {
    workerMediator.notifyOthers(this, "Message from " + name);
  }

  @Override
  public void receiveMessage(String message) {
    System.out.println(name + " " + "received message " + message);
  }
}

abstract class WorkerMediator {
  protected List<Worker> workers = new ArrayList<>();

  abstract void notifyOthers(Worker sender, String message);

  void addWorker(Worker worker) {
    workers.add(worker);
  }
}

class DoSomeJobMediatorImpl extends WorkerMediator {

  @Override
  public void notifyOthers(Worker sender, String message) {
    System.out.println("Doing something from do job work mediator");
    workers.stream()
        .filter(worker1 -> !worker1.equals(sender))
        .collect(Collectors.toSet())
        .forEach(worker1 -> worker1.receiveMessage("some regular message"));
  }
}

class DoSomeJob2MediatorImpl extends WorkerMediator {

  @Override
  public void notifyOthers(Worker sender, String message) {
    System.out.println("Doing something from do job 2 work mediator");
    workers.stream()
        .filter(worker1 -> !worker1.equals(sender))
        .collect(Collectors.toSet())
        .forEach(worker1 -> worker1.receiveMessage("some regular message"));
  }
}
