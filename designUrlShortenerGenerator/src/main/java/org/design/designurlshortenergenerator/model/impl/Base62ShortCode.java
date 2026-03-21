package org.design.designurlshortenergenerator.model.impl;

import org.design.designurlshortenergenerator.model.api.ShortCode;
import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;

public class Base62ShortCode implements ShortCode {

    private final String value;

    public Base62ShortCode(String value) {
        this.value = value;
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