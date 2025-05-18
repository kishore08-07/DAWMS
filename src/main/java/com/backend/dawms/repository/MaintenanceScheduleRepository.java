package com.backend.dawms.repository;

import com.backend.dawms.model.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {
    List<MaintenanceSchedule> findByAssetId(Long assetId);
    
    @Query("SELECT m FROM MaintenanceSchedule m WHERE m.scheduledDate BETWEEN :startDate AND :endDate AND m.status = 'PENDING' AND m.notificationSent = false")
    List<MaintenanceSchedule> findUpcomingMaintenance(LocalDate startDate, LocalDate endDate);
    
    List<MaintenanceSchedule> findByStatusAndScheduledDateBefore(String status, LocalDate date);

    List<MaintenanceSchedule> findByScheduledDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);

    List<MaintenanceSchedule> findByAssignedTechnicianIdAndStatusNot(Long technicianId, String status);
}