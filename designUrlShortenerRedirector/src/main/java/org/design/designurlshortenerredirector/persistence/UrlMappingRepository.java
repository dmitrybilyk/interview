package org.design.designurlshortenerredirector.persistence;

//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
//    Optional<UrlMapping> findByShortCode(String shortCode);
//}

// Assuming UrlMapping is the entity class
import org.design.designurlshortenerredirector.persistence.model.UrlMapping;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UrlMappingRepository extends ReactiveCrudRepository<UrlMapping, Long> {
    // Returns Mono<UrlMapping> for a single result
    Mono<UrlMapping> findByShortCode(String shortCode);
}