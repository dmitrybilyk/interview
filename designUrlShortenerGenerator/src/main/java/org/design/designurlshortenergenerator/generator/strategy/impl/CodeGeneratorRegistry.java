package org.design.designurlshortenergenerator.generator.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.design.designurlshortenergenerator.generator.strategy.api.CodeGeneratorStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodeGeneratorRegistry {

    // Spring injects all beans implementing the interface into this map
    private final Map<String, CodeGeneratorStrategy> strategies;

    public CodeGeneratorStrategy getStrategy(String type) {
        CodeGeneratorStrategy strategy = strategies.get(type);
        if (strategy == null) {
            // Fallback or Exception
            return strategies.get("base62CodeGeneratorStrategy");
        }
        return strategy;
    }
}