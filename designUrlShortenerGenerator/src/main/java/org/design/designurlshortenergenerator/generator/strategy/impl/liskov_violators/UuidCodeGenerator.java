package org.design.designurlshortenergenerator.generator.strategy.impl.liskov_violators;

import org.design.designurlshortenergenerator.generator.strategy.api.CodeGeneratorStrategy;
import org.springframework.stereotype.Component;

@Component
public class UuidCodeGenerator implements CodeGeneratorStrategy {
    @Override
    public String encode(long id) {
        // Ми повністю ігноруємо вхідний параметр 'id'.
        // Це порушує очікування клієнта (сервісу), що код базується на наданому ID.
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}