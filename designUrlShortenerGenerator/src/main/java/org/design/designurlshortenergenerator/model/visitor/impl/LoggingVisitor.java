package org.design.designurlshortenergenerator.model.visitor.impl;

import org.design.designurlshortenergenerator.model.impl.Base62ShortCode;
import org.design.designurlshortenergenerator.model.impl.CustomAliasShortCode;
import org.design.designurlshortenergenerator.model.impl.ExpiringShortCode;
import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;

public class LoggingVisitor implements ShortCodeVisitor {
    @Override
    public void visit(Base62ShortCode code) {
        System.out.println("Visiting Base62ShortCode" + code.getValue());
    }

    @Override
    public void visit(CustomAliasShortCode code) {
        System.out.println("Visiting CustomAliasShortCode" + code.getValue());
    }

    @Override
    public void visit(ExpiringShortCode code) {
        System.out.println("Visiting ExpiringShortCode" + code.getValue());
    }
}
