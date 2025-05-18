package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardStatsDTO {
    private AssetStats assetStats;
    private WarrantyStats warrantyStats;
    private MaintenanceStats maintenanceStats;
    private ComplianceStats complianceStats;
    private List<AlertDTO> recentAlerts;
    private List<AuditLogDTO> recentActivities;
} 