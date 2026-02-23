package org.design.designurlshortenergenerator.persistance.repository;

import org.design.designurlshortenergenerator.persistance.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
}
