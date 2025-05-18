package com.backend.dawms.controller;

import com.backend.dawms.model.*;
import com.backend.dawms.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Slf4j
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenanceSchedule> scheduleMaintenanceTask(
            @Valid @RequestBody MaintenanceSchedule task) {
        return ResponseEntity.ok(maintenanceService.scheduleMaintenanceTask(task));
    }

    @PostMapping("/{taskId}/logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenanceLog> addMaintenanceLog(
            @PathVariable Long taskId,
            @Valid @RequestBody MaintenanceLog log) {
        return ResponseEntity.ok(maintenanceService.addMaintenanceLog(taskId, log));
    }

    @PostMapping("/{taskId}/parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<SparePartUsage> recordSparePartUsage(
            @PathVariable Long taskId,
            @Valid @RequestBody SparePartUsage usage) {
        return ResponseEntity.ok(maintenanceService.recordSparePartUsage(taskId, usage));
    }

    @PutMapping("/{taskId}/technician/{technicianId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaintenanceSchedule> assignTechnician(
            @PathVariable Long taskId,
            @PathVariable Long technicianId) {
        return ResponseEntity.ok(maintenanceService.assignTechnician(taskId, technicianId));
    }

    @PutMapping("/{taskId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<MaintenanceSchedule> completeMaintenanceTask(
            @PathVariable Long taskId,
            @RequestBody String completionNotes) {
        return ResponseEntity.ok(maintenanceService.completeMaintenanceTask(taskId, completionNotes));
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<List<MaintenanceSchedule>> getUpcomingTasks(
            @RequestParam(defaultValue = "7") int daysAhead) {
        return ResponseEntity.ok(maintenanceService.findUpcomingMaintenanceTasks(daysAhead));
    }

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<List<MaintenanceSchedule>> getTasksByTechnician(
            @PathVariable Long technicianId) {
        return ResponseEntity.ok(maintenanceService.findMaintenanceTasksByTechnician(technicianId));
    }
}