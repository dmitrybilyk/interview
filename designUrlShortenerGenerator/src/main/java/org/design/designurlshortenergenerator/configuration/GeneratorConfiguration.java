package org.design.designurlshortenergenerator.configuration;

import org.design.designurlshortenergenerator.generator.allocator.RangeAllocator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneratorConfiguration {

    @Value("${ZK_CONNECT:localhost:2181}")
    private String zk;

    @Bean
    public RangeAllocator rangeAllocator() throws Exception {
        return new RangeAllocator(zk);
    }

}
