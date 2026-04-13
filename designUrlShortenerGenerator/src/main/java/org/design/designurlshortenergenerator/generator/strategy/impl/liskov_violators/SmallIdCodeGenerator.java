package org.design.designurlshortenergenerator.generator.strategy.impl.liskov_violators;

import org.design.designurlshortenergenerator.generator.strategy.api.CodeGeneratorStrategy;
import org.springframework.stereotype.Component;

@Component
public class SmallIdCodeGenerator implements CodeGeneratorStrategy {
    @Override
    public String encode(long id) {
        // Посилення передмови: ми приймаємо тільки маленькі числа
        if (id > 1000) {
            throw new IllegalArgumentException("ID too large for this strategy!");
        }
        return "small-" + id;
    }
}