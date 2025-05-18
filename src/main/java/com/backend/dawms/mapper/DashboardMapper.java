package com.backend.dawms.mapper;

import com.backend.dawms.dto.*;
import com.backend.dawms.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DashboardMapper {
    
    public static WarrantyAlertDTO mapToWarrantyAlertDTO(Warranty warranty) {
        return WarrantyAlertDTO.builder()
            .assetId(warranty.getAsset().getId())
            .assetName(warranty.getAsset().getName())
            .expiryDate(warranty.getExpiryDate())
            .daysUntilExpiry(warranty.getDaysUntilExpiry())
            .build();
    }

    public static MaintenanceTaskDTO mapToMaintenanceTaskDTO(MaintenanceSchedule schedule) {
        return MaintenanceTaskDTO.builder()
            .taskId(schedule.getId())
            .assetId(schedule.getAsset().getId())
            .assetName(schedule.getAsset().getName())
            .taskDescription(schedule.getTaskDescription())
            .scheduledDate(schedule.getScheduledDate())
            .priority(schedule.getPriority())
            .assignedTechnicianId(schedule.getAssignedTechnician().getId())
            .assignedTechnicianName(schedule.getAssignedTechnician().getName())
            .status(schedule.getStatus())
            .build();
    }

    public static ComplianceAlertDTO mapToComplianceAlertDTO(ComplianceRecord record) {
        return ComplianceAlertDTO.builder()
            .recordId(record.getId())
            .assetId(record.getAsset().getId())
            .assetName(record.getAsset().getName())
            .complianceType(record.getComplianceType())
            .nextReviewDate(record.getNextReviewDate())
            .priority(record.getCompliancePriority())
            .status(record.getStatus())
            .build();
    }

    public static AuditLogDTO mapToAuditLogDTO(AuditLog log) {
        return AuditLogDTO.builder()
            .id(log.getId())
            .timestamp(log.getTimestamp())
            .eventType(log.getEventType())
            .eventDescription(log.getEventDescription())
            .performedBy(log.getPerformedBy().getName())
            .performedById(log.getPerformedBy().getId())
            .build();
    }
} 