package org.design.designurlshortenergenerator.persistence.repository;

import org.design.designurlshortenergenerator.persistence.model.UrlMapping;
import org.design.designurlshortenergenerator.persistence.repository.api.UrlDeleter;
import org.design.designurlshortenergenerator.persistence.repository.api.UrlReader;
import org.design.designurlshortenergenerator.persistence.repository.api.UrlSaver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// it's overkill to have this interface separated to many interfaces
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long>, UrlSaver, UrlReader, UrlDeleter {
    @Override
    default void saveMapping(UrlMapping mapping) {
        save(mapping);
    }

    @Override
    Optional<UrlMapping> findByShortCode(String code); // для UrlReader
}
