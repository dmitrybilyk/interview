package org.design.designurlshortenergenerator.model.impl;

import org.design.designurlshortenergenerator.model.api.ShortCode;
import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;

public class CustomAliasShortCode implements ShortCode {

    private final String value;

    public CustomAliasShortCode(String value) {
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