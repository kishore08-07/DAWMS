package com.backend.dawms.controller;

import com.backend.dawms.dto.*;
import com.backend.dawms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'TECHNICIAN')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'TECHNICIAN')")
    public ResponseEntity<DashboardStatsDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/assets")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'TECHNICIAN')")
    public ResponseEntity<AssetStats> getAssetStats() {
        return ResponseEntity.ok(dashboardService.getAssetStatistics());
    }

    @GetMapping("/warranties")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'TECHNICIAN')")
    public ResponseEntity<WarrantyStats> getWarrantyStats() {
        return ResponseEntity.ok(dashboardService.getWarrantyStatistics());
    }

    @GetMapping("/maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'TECHNICIAN')")
    public ResponseEntity<MaintenanceStats> getMaintenanceStats() {
        return ResponseEntity.ok(dashboardService.getMaintenanceStatistics());
    }

    @GetMapping("/compliance")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'TECHNICIAN')")
    public ResponseEntity<ComplianceStats> getComplianceStats() {
        return ResponseEntity.ok(dashboardService.getComplianceStatistics());
    }

    @GetMapping("/audit/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<AuditExportDTO> exportAuditLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "ALL") String exportType,
            @RequestParam(defaultValue = "CSV") String format) {
        
        AuditExportDTO export = AuditExportDTO.builder()
            .startDate(startDate)
            .endDate(endDate)
            .exportType(exportType)
            .format(format)
            .generatedAt(LocalDateTime.now())
            .status("PROCESSING")
            .build();
            
        // TODO: Implement async export processing
        return ResponseEntity.accepted().body(export);
    }

    @GetMapping("/compliance/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMPLIANCE_OFFICER')")
    public ResponseEntity<ComplianceStats> getComplianceSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardStats().getComplianceStats());
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<AlertDTO>> getActiveAlerts() {
        return ResponseEntity.ok(dashboardService.getDashboardStats().getRecentAlerts());
    }

    @GetMapping("/activities")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'TECHNICIAN')")
    public ResponseEntity<List<AuditLogDTO>> getRecentActivities() {
        return ResponseEntity.ok(dashboardService.getRecentActivities());
    }
} 