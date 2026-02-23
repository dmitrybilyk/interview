package org.design.designurlshortenergenerator.generator.strategy.impl;

import org.design.designurlshortenergenerator.generator.strategy.api.CodeGeneratorStrategy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.nio.ByteBuffer;
import java.util.Base64;

@Component
@Primary
public class Base64CodeGenerator implements CodeGeneratorStrategy {
    
    @Override
    public String encode(long id) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(id);
        byte[] bytes = buffer.array();
        
        int firstNonZero = 0;
        while (firstNonZero < bytes.length && bytes[firstNonZero] == 0) {
            firstNonZero++;
        }
        
        byte[] resultBytes = new byte[bytes.length - firstNonZero];
        System.arraycopy(bytes, firstNonZero, resultBytes, 0, resultBytes.length);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(resultBytes);
    }
}