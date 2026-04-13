package org.design.designurlshortenergenerator.service.audit;

import org.design.designurlshortenergenerator.exceptions.SomeBusinessException;
import org.design.designurlshortenergenerator.persistence.model.sql.AuditLog;
import org.design.designurlshortenergenerator.persistence.repository.sql.AuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.NESTED)
    public void saveAudit(String message) {
        auditRepository.save(new AuditLog(message));
    }
}