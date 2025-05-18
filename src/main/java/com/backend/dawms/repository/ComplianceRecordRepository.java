package com.backend.dawms.repository;

import com.backend.dawms.model.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByAssetId(Long assetId);
    
    @Query("SELECT COUNT(c) FROM ComplianceRecord c WHERE c.status = 'NON_COMPLIANT'")
    long countNonCompliantRecords();
    
    @Query("SELECT COUNT(c) FROM ComplianceRecord c WHERE c.status = 'PENDING_REVIEW'")
    long countPendingReviewRecords();
    
    @Query("SELECT COUNT(c) FROM ComplianceRecord c WHERE c.nextReviewDate < CURRENT_TIMESTAMP")
    long countExpiringCertificates();
    
    @Query("SELECT COUNT(c) FROM ComplianceRecord c WHERE c.checkDate < CURRENT_TIMESTAMP AND c.status = 'PENDING'")
    long countOverdueChecks();
    
    List<ComplianceRecord> findByCheckDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT c FROM ComplianceRecord c WHERE c.nextReviewDate BETWEEN CURRENT_TIMESTAMP AND :endDate ORDER BY c.nextReviewDate ASC")
    List<ComplianceRecord> findUpcomingDeadlines(LocalDateTime endDate);
} 