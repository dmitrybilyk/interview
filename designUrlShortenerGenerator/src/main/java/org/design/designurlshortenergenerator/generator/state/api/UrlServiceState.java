package org.design.designurlshortenergenerator.generator.state.api;

import org.design.designurlshortenergenerator.service.impl.UrlGeneratorServiceImpl;

public interface UrlServiceState {
    String shorten(UrlGeneratorServiceImpl context, String originalUrl);
}