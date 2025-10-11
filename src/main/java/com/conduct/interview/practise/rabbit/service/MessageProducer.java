//package com.conduct.interview.practise.rabbit.service;
//
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//
//@Service
////@Profile("rabbit")
//public class MessageProducer {
//  @Autowired private RabbitTemplate rabbitTemplate;
//
//  private static final String EXCHANGE_NAME = "my_exchange";
//  private static final String ROUTING_KEY = "my_routing_key";
//
//  public void sendMessage(String message) {
//    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
//    System.out.println("Message sent: " + message);
//  }
//}
