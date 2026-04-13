package org.design.designurlshortenergenerator.model.visitor.api;

import org.design.designurlshortenergenerator.model.impl.Base62ShortCode;
import org.design.designurlshortenergenerator.model.impl.CustomAliasShortCode;
import org.design.designurlshortenergenerator.model.impl.ExpiringShortCode;

public interface ShortCodeVisitor {

    void visit(Base62ShortCode code);

    void visit(CustomAliasShortCode code);

    void visit(ExpiringShortCode code);
}