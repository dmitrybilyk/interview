package com.conduct.interview.messaging.kafka.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import java.nio.charset.StandardCharsets;

public class PersonDeserializer implements Deserializer<Person> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Person deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;
            return mapper.readValue(
                    new String(data, StandardCharsets.UTF_8),
                    Person.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Person", e);
        }
    }
}
