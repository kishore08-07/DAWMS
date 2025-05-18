package com.backend.dawms.repository;

import com.backend.dawms.model.SparePartUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SparePartUsageRepository extends JpaRepository<SparePartUsage, Long> {
    List<SparePartUsage> findByMaintenanceScheduleId(Long scheduleId);
    
    List<SparePartUsage> findBySparePartId(Long sparePartId);
    
    @Query("SELECT spu FROM SparePartUsage spu WHERE spu.maintenanceSchedule.asset.id = ?1")
    List<SparePartUsage> findByAssetId(Long assetId);
    
    List<SparePartUsage> findByUsedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
} 