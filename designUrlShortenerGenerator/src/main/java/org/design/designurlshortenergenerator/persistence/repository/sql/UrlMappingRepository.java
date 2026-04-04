package org.design.designurlshortenergenerator.persistence.repository.sql;

import org.design.designurlshortenergenerator.persistence.model.sql.UrlMapping;
import org.design.designurlshortenergenerator.persistence.repository.sql.api.UrlDeleter;
import org.design.designurlshortenergenerator.persistence.repository.sql.api.UrlReader;
import org.design.designurlshortenergenerator.persistence.repository.sql.api.UrlSaver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// it's overkill to have this interface separated to many interfaces
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long>, UrlSaver, UrlReader, UrlDeleter {
    @Override
    default void saveMapping(UrlMapping mapping) {
        save(mapping);
    }

    @Override
    Optional<UrlMapping> findByShortCode(String code); // для UrlReader

    @Modifying
    @Transactional
    @Query("UPDATE UrlMapping u SET u.clickCount = u.clickCount + 1 WHERE u.shortCode = :code")
    void incrementClicksAtomic(@Param("code") String code);
}
