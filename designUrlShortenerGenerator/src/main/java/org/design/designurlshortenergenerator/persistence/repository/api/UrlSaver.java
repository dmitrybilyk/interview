package org.design.designurlshortenergenerator.persistence.repository.api;

import org.design.designurlshortenergenerator.persistence.model.UrlMapping;

public interface UrlSaver {
    void saveMapping(UrlMapping mapping);
}