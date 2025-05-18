package com.backend.dawms.service;

import com.backend.dawms.dto.*;
import com.backend.dawms.mapper.DashboardMapper;
import com.backend.dawms.repository.AssetRepository;
import com.backend.dawms.repository.WarrantyRepository;
import com.backend.dawms.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final AssetRepository assetRepository;
    private final WarrantyRepository warrantyRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final DepreciationService depreciationService;

    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
                .assetStats(getAssetStatistics())
                .warrantyStats(getWarrantyStats())
                .maintenanceStats(getMaintenanceStatistics())
                .build();
    }

    public AssetStats getAssetStatistics() {
        return AssetStats.builder()
            .totalAssets(assetRepository.count())
            .activeAssets(assetRepository.countByStatus("ACTIVE"))
            .inactiveAssets(assetRepository.countByStatus("INACTIVE"))
            .availableAssets(assetRepository.countByStatus("AVAILABLE"))
            .assignedAssets(assetRepository.countByStatus("ASSIGNED"))
            .maintenanceAssets(assetRepository.countByStatus("MAINTENANCE"))
            .totalValue(depreciationService.getTotalAssetValue())
            .depreciatedValue(depreciationService.getTotalDepreciatedValue())
            .build();
    }

    private WarrantyStats getWarrantyStats() {
        LocalDate now = LocalDate.now();
        LocalDate endOfMonth = now.plusMonths(1).withDayOfMonth(1).minusDays(1);

        return WarrantyStats.builder()
                .activeWarranties(warrantyRepository.countByExpiryDateAfter(now))
                .expiringThisMonth(warrantyRepository.countByExpiryDateBetween(now, endOfMonth))
                .build();
    }

    public MaintenanceStats getMaintenanceStatistics() {
        return MaintenanceStats.builder()
            .scheduledTasks(maintenanceRepository.countScheduledTasks())
            .overdueTasks(maintenanceRepository.countOverdueTasks())
            .completedTasks(maintenanceRepository.countCompletedTasksThisMonth())
            .upcomingTasks(maintenanceRepository.findUpcomingTasks().stream()
                .map(DashboardMapper::mapToMaintenanceTaskDTO)
                .toList())
            .build();
    }

    public List<AlertDTO> getActiveAlerts() {
        List<AlertDTO> alerts = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysLater = now.plusDays(30);

        // Add warranty alerts
        warrantyRepository.findUpcomingExpirations(now, thirtyDaysLater)
            .forEach(warranty -> {
                alerts.add(AlertDTO.builder()
                    .type("WARRANTY")
                    .assetName(warranty.getAsset().getName())
                    .message("Warranty expiring soon")
                    .dueDate(warranty.getExpiryDate())
                    .priority(getPriorityForWarranty(warranty.getExpiryDate()))
                    .build());
            });

        // Add maintenance alerts
        maintenanceRepository.findUpcomingMaintenance(now, thirtyDaysLater)
            .forEach(maintenance -> {
                alerts.add(AlertDTO.builder()
                    .type("MAINTENANCE")
                    .assetName(maintenance.getAsset().getName())
                    .message("Scheduled maintenance due")
                    .dueDate(maintenance.getScheduledDate())
                    .priority(getPriorityForMaintenance(maintenance.getScheduledDate()))
                    .build());
            });

        return alerts;
    }

    private String getPriorityForWarranty(LocalDate expiryDate) {
        long daysUntilExpiry = LocalDate.now().until(expiryDate).getDays();
        if (daysUntilExpiry <= 7) return "HIGH";
        if (daysUntilExpiry <= 14) return "MEDIUM";
        return "LOW";
    }

    private String getPriorityForMaintenance(LocalDate dueDate) {
        long daysUntilDue = LocalDate.now().until(dueDate).getDays();
        if (daysUntilDue <= 3) return "HIGH";
        if (daysUntilDue <= 7) return "MEDIUM";
        return "LOW";
    }
} 