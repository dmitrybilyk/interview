package org.design.designurlshortenerredirector.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "url.exchange";
    public static final String QUEUE_NAME = "url.shorten.queue";
    public static final String ROUTING_KEY = "url.created";

    // 1. Define the Exchange
    @Bean
    public TopicExchange urlExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 2. Define the Queue
    @Bean
    public Queue urlQueue() {
        return new Queue(QUEUE_NAME, true); // true = durable (survives rabbit restart)
    }

    // 3. Bind the Queue to the Exchange with a Routing Key
    @Bean
    public Binding binding(Queue urlQueue, TopicExchange urlExchange) {
        return BindingBuilder.bind(urlQueue).to(urlExchange).with(ROUTING_KEY);
    }
}