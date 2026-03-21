package org.design.designurlshortenergenerator.model.visitor.impl;

import io.micrometer.core.instrument.MeterRegistry;
import org.design.designurlshortenergenerator.model.impl.Base62ShortCode;
import org.design.designurlshortenergenerator.model.impl.CustomAliasShortCode;
import org.design.designurlshortenergenerator.model.impl.ExpiringShortCode;
import org.design.designurlshortenergenerator.model.visitor.api.ShortCodeVisitor;

public class MetricsVisitor implements ShortCodeVisitor {

    private final MeterRegistry meterRegistry;

    public MetricsVisitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void visit(Base62ShortCode code) {
        meterRegistry.counter("shortcode.base62").increment();
    }

    @Override
    public void visit(CustomAliasShortCode code) {
        meterRegistry.counter("shortcode.alias").increment();
    }

    @Override
    public void visit(ExpiringShortCode code) {
        meterRegistry.counter("shortcode.expiring").increment();
    }
}