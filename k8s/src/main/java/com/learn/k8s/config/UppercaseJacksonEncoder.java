package com.learn.k8s.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;

import java.io.IOException;

public class UppercaseJacksonEncoder extends Jackson2JsonEncoder {

    public UppercaseJacksonEncoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(modifyMapper(mapper), mimeTypes);
    }

    private static ObjectMapper modifyMapper(ObjectMapper mapper) {
        // Створюємо копію мапера, щоб не зіпсувати глобальний, 
        // і додаємо модуль для перетворення рядків
        ObjectMapper uppercaseMapper = mapper.copy();
        SimpleModule module = new SimpleModule();
        
        module.addSerializer(String.class, new JsonSerializer<>() {
            @Override
            public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value != null ? value.toUpperCase() : null);
            }
        });
        
        return uppercaseMapper.registerModule(module);
    }
}