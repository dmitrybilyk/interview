//package com.conduct.interview.practise.rabbit.configuration;
//
//import org.springframework.amqp.core.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//@Configuration
////@Profile("rabbit")
//public class RabbitConfig {
//
//  private static final String QUEUE_NAME = "my_queue";
//  private static final String EXCHANGE_NAME = "my_exchange";
//
//  @Bean
//  public Queue queue() {
//    return new Queue(QUEUE_NAME, false);
//  }
//
//  @Bean
//  public DirectExchange exchange() {
//    return new DirectExchange(EXCHANGE_NAME);
//  }
//
//  @Bean
//  public Binding binding(Queue queue, DirectExchange exchange) {
//    return BindingBuilder.bind(queue).to(exchange).with("my_routing_key");
//  }
//}
