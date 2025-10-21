package com.conduct.interview.messaging.kafka.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import java.nio.charset.StandardCharsets;

public class PersonSerializer implements Serializer<Person> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Person data) {
        try {
            if (data == null) return null;
            return mapper.writeValueAsString(data)
                    .getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Person", e);
        }
    }
}
