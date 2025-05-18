package com.backend.dawms.repository;

import com.backend.dawms.model.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    List<MaintenanceLog> findByMaintenanceScheduleId(Long scheduleId);
    
    @Query("SELECT ml FROM MaintenanceLog ml WHERE ml.maintenanceSchedule.asset.id = ?1")
    List<MaintenanceLog> findByAssetId(Long assetId);
    
    List<MaintenanceLog> findByLoggedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(ml) FROM MaintenanceLog ml WHERE ml.action = 'COMPLETED'")
    long countCompletedMaintenance();
} 