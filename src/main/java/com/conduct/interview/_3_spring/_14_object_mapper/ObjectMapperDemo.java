package com.conduct.interview._3_spring._14_object_mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ObjectMapperDemo {

    public static void main(String[] args) throws Exception {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_14_object_mapper/object-mapper-context.xml")) {

            ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

            User user = new User("alice", 3);
            String json = objectMapper.writeValueAsString(user);
            System.out.println("serialized (camelCase field -> snake_case JSON): " + json);

            User roundTripped = objectMapper.readValue(json, User.class);
            System.out.println("deserialized back: " + roundTripped);

            String rawSnakeCaseJson = "{\"user_name\":\"bob\",\"account_age\":7}";
            User fromRawJson = objectMapper.readValue(rawSnakeCaseJson, User.class);
            System.out.println("parsed hand-written snake_case JSON -> " + fromRawJson);
        }
    }
}
