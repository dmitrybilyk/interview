package org.design.designurlshortenergenerator.generator.state.impl;

import org.design.designurlshortenergenerator.generator.state.api.UrlServiceState;
import org.design.designurlshortenergenerator.service.impl.UrlGeneratorServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceState implements UrlServiceState {

    @Override
    public String shorten(UrlGeneratorServiceImpl context, String originalUrl) {
        throw new RuntimeException("Service is currently in Maintenance Mode. New URLs cannot be generated.");
    }
}