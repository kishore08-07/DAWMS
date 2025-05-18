package com.backend.dawms.service;

import com.backend.dawms.dto.*;
import com.backend.dawms.mapper.DashboardMapper;
import com.backend.dawms.model.*;
import com.backend.dawms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final AssetRepository assetRepository;
    private final WarrantyRepository warrantyRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final ComplianceRecordRepository complianceRepository;
    private final AuditLogRepository auditLogRepository;
    private final AssetDepreciationRepository depreciationRepository;

    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
            .assetStats(getAssetStatistics())
            .warrantyStats(getWarrantyStatistics())
            .maintenanceStats(getMaintenanceStatistics())
            .complianceStats(getComplianceStatistics())
            .recentActivities(getRecentActivities())
            .build();
    }

    public AssetStats getAssetStatistics() {
        BigDecimal totalValue = Optional.ofNullable(depreciationRepository.calculateTotalCurrentValue())
            .orElse(BigDecimal.ZERO);
        BigDecimal depreciatedValue = getTotalDepreciatedValue();
        
        return AssetStats.builder()
            .totalAssets(assetRepository.count())
            .activeAssets(assetRepository.countByStatus("ACTIVE"))
            .inactiveAssets(assetRepository.countByStatus("INACTIVE"))
            .availableAssets(assetRepository.countByStatus("AVAILABLE"))
            .assignedAssets(assetRepository.countByStatus("ASSIGNED"))
            .maintenanceAssets(assetRepository.countByStatus("MAINTENANCE"))
            .totalValue(totalValue)
            .depreciatedValue(depreciatedValue)
            .build();
    }

    public WarrantyStats getWarrantyStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);

        return WarrantyStats.builder()
            .activeWarranties(warrantyRepository.countActiveWarranties())
            .expiringThisMonth(warrantyRepository.countByExpiryDateBetween(today, today.plusMonths(1)))
            .expiredWarranties(warrantyRepository.countExpiredWarranties())
            .upcomingExpirations(warrantyRepository.findUpcomingExpirations(today, thirtyDaysFromNow)
                .stream()
                .map(DashboardMapper::mapToWarrantyAlertDTO)
                .collect(Collectors.toList()))
            .build();
    }

    public MaintenanceStats getMaintenanceStatistics() {
        long total = maintenanceRepository.countScheduledTasks() + maintenanceRepository.countOverdueTasks();
        long completed = maintenanceRepository.countCompletedTasksThisMonth();
        double completionRate = total > 0 
            ? BigDecimal.valueOf(completed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                .doubleValue()
            : 0.0;

        return MaintenanceStats.builder()
            .scheduledTasks(maintenanceRepository.countScheduledTasks())
            .overdueTasks(maintenanceRepository.countOverdueTasks())
            .completedTasks(completed)
            .upcomingTasks(maintenanceRepository.findUpcomingTasks()
                .stream()
                .map(DashboardMapper::mapToMaintenanceTaskDTO)
                .collect(Collectors.toList()))
            .completionRate(completionRate)
            .build();
    }

    public ComplianceStats getComplianceStatistics() {
        LocalDateTime thirtyDaysFromNow = LocalDateTime.now().plusDays(30);
        long total = complianceRepository.count();
        long nonCompliant = complianceRepository.countNonCompliantRecords();
        double complianceRate = total > 0 
            ? BigDecimal.valueOf(total - nonCompliant)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                .doubleValue()
            : 100.0;
        
        return ComplianceStats.builder()
            .totalRecords(total)
            .pendingReviews(complianceRepository.countPendingReviewRecords())
            .expiringCertificates(complianceRepository.countExpiringCertificates())
            .overdueChecks(complianceRepository.countOverdueChecks())
            .upcomingDeadlines(complianceRepository.findUpcomingDeadlines(thirtyDaysFromNow)
                .stream()
                .map(DashboardMapper::mapToComplianceAlertDTO)
                .collect(Collectors.toList()))
            .complianceRate(complianceRate)
            .build();
    }

    public List<AuditLogDTO> getRecentActivities() {
        return auditLogRepository.findLatestByEventType("ACTIVITY")
            .stream()
            .map(DashboardMapper::mapToAuditLogDTO)
            .collect(Collectors.toList());
    }

    private BigDecimal getTotalDepreciatedValue() {
        BigDecimal totalAssetValue = Optional.ofNullable(depreciationRepository.calculateTotalCurrentValue())
            .orElse(BigDecimal.ZERO);
        BigDecimal totalInitialValue = Optional.ofNullable(assetRepository.calculateTotalAssetValue())
            .orElse(BigDecimal.ZERO);
        return totalInitialValue.subtract(totalAssetValue).setScale(2, RoundingMode.HALF_UP);
    }
}