package org.design.designurlshortenergenerator.model.impl;

import org.design.designurlshortenergenerator.model.api.ShortCode;
import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;

public class ExpiringShortCode implements ShortCode {

    private final String value;
    private final long expiresAt;

    public ExpiringShortCode(String value, long expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void accept(ShortCodeVisitor visitor) {
        visitor.visit(this);
    }
}