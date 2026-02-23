package org.design.designurlshortenergenerator.generator.strategy.impl;

import org.design.designurlshortenergenerator.generator.strategy.utils.Base62;
import org.design.designurlshortenergenerator.generator.strategy.api.CodeGeneratorStrategy;
import org.springframework.stereotype.Component;

@Component
public class Base62CodeGeneratorStrategy implements CodeGeneratorStrategy {
    @Override
    public String encode(long id) {
        return Base62.encode(id);
    }
}