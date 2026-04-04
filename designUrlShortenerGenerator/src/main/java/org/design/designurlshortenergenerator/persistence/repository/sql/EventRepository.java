package org.design.designurlshortenergenerator.persistence.repository.sql;

import org.design.designurlshortenergenerator.persistence.model.sql.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {}
