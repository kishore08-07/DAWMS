package com.backend.dawms.repository;

import com.backend.dawms.model.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceSchedule, Long> {
    
    @Query("SELECT COUNT(m) FROM MaintenanceSchedule m WHERE m.status = 'PENDING' OR m.status = 'IN_PROGRESS'")
    long countScheduledTasks();
    
    @Query("SELECT COUNT(m) FROM MaintenanceSchedule m WHERE m.scheduledDate < CURRENT_DATE AND m.status != 'COMPLETED'")
    long countOverdueTasks();
    
    @Query("SELECT COUNT(m) FROM MaintenanceSchedule m WHERE m.status = 'COMPLETED' " +
           "AND YEAR(m.completedAt) = YEAR(CURRENT_DATE) AND MONTH(m.completedAt) = MONTH(CURRENT_DATE)")
    long countCompletedTasksThisMonth();
    
    @Query("SELECT COALESCE(SUM(m.actualCost), 0) FROM MaintenanceSchedule m " +
           "WHERE m.status = 'COMPLETED' " +
           "AND YEAR(m.completedAt) = YEAR(CURRENT_DATE) AND MONTH(m.completedAt) = MONTH(CURRENT_DATE)")
    BigDecimal calculateMaintenanceCostsForCurrentMonth();
    
    @Query("SELECT m FROM MaintenanceSchedule m " +
           "WHERE m.scheduledDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.scheduledDate ASC")
    List<MaintenanceSchedule> findUpcomingMaintenance(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
    List<MaintenanceSchedule> findByAssetId(Long assetId);
    
    List<MaintenanceSchedule> findByAssignedTechnicianIdAndScheduledDateBetween(
        Long technicianId, LocalDate startDate, LocalDate endDate);
        
    List<MaintenanceSchedule> findByScheduledDateBetweenAndStatus(
        LocalDate startDate, LocalDate endDate, String status);
    
    List<MaintenanceSchedule> findByAssignedTechnicianIdAndStatusNot(Long technicianId, String status);
    
    @Query("SELECT m FROM MaintenanceSchedule m WHERE m.scheduledDate > CURRENT_DATE ORDER BY m.scheduledDate ASC")
    List<MaintenanceSchedule> findUpcomingTasks();
    
    List<MaintenanceSchedule> findByStatusAndScheduledDateBefore(String status, LocalDate date);
} 