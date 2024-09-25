package com.conduct.interview.practise.rabbit.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbit")
public class MessageConsumer {

  @RabbitListener(queues = "my_queue")
  public void receiveMessage(String message) {
    System.out.println("Received message: " + message);
  }
}
