package org.design.designurlshortenergenerator.persistence.repository.sql.api;

import org.design.designurlshortenergenerator.persistence.model.sql.UrlMapping;

public interface UrlSaver {
    void saveMapping(UrlMapping mapping);
}