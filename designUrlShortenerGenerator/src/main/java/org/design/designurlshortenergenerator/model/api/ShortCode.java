package org.design.designurlshortenergenerator.model.api;

import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;

public interface ShortCode {
    String getValue();
    void accept(ShortCodeVisitor visitor);
}