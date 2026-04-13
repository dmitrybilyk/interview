package com.learn.k8s.brokers.kafka.single_topic.user;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class UserProducer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        try(KafkaProducer<String,String> producer = new KafkaProducer<>(props)){

            for(int i=1;i<=10;i++){

                String json = """
                        {
                          "eventType":"USER_CREATED",
                          "userId":"user-%d",
                          "name":"John-%d"
                        }
                        """.formatted(i,i);

                ProducerRecord<String,String> record =
                        new ProducerRecord<>("events-topic", json);

                producer.send(record);

                System.out.println("👤 USER event sent: "+json);
            }

            producer.flush();
        }
    }
}