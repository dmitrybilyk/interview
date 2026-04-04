package org.design.designurlshortenergenerator.persistence.repository.sql;

import org.design.designurlshortenergenerator.persistence.model.sql.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuditRepository extends JpaRepository<AuditLog, Long> {}
