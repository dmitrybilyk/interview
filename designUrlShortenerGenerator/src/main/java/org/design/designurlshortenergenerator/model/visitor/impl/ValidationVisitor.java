package org.design.designurlshortenergenerator.model.visitor.impl;

import org.design.designurlshortenergenerator.model.impl.Base62ShortCode;
import org.design.designurlshortenergenerator.model.impl.CustomAliasShortCode;
import org.design.designurlshortenergenerator.model.impl.ExpiringShortCode;
import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;

public class ValidationVisitor implements ShortCodeVisitor {

    @Override
    public void visit(Base62ShortCode code) {
        if (!code.getValue().matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Invalid Base62 code");
        }
    }

    @Override
    public void visit(CustomAliasShortCode code) {
        if (code.getValue().length() < 3) {
            throw new IllegalArgumentException("Alias too short");
        }
    }

    @Override
    public void visit(ExpiringShortCode code) {
        if (code.getExpiresAt() < System.currentTimeMillis()) {
            throw new IllegalStateException("Code already expired");
        }
    }
}