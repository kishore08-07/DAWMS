package com.backend.dawms.repository;

import com.backend.dawms.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditLog a WHERE a.eventType = ?1 ORDER BY a.timestamp DESC")
    List<AuditLog> findLatestByEventType(String eventType);
    
    List<AuditLog> findByPerformedBy_Name(String username);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.eventType = 'SECURITY_VIOLATION'")
    long countSecurityViolations();
    
    List<AuditLog> findByEventTypeAndTimestampBetween(
        String eventType, LocalDateTime startDate, LocalDateTime endDate);
} 