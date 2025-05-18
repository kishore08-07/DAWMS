package com.backend.dawms.service;

import com.backend.dawms.exception.MaintenanceException;
import com.backend.dawms.model.*;
import com.backend.dawms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaintenanceService {
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final SparePartRepository sparePartRepository;
    private final SparePartUsageRepository sparePartUsageRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetRepository assetRepository;

    public MaintenanceSchedule scheduleMaintenanceTask(MaintenanceSchedule task) {
        // Validate asset exists
        Asset asset = assetRepository.findById(task.getAsset().getId())
            .orElseThrow(() -> new MaintenanceException("Asset not found"));

        // Validate technician exists if assigned
        if (task.getAssignedTechnician() != null) {
            employeeRepository.findById(task.getAssignedTechnician().getId())
                .orElseThrow(() -> new MaintenanceException("Technician not found"));
        }

        // Set default values
        task.setStatus("PENDING");
        task.setNotificationSent(false);
        
        log.info("Scheduling maintenance task for asset: {}", asset.getId());
        return maintenanceScheduleRepository.save(task);
    }

    public MaintenanceLog addMaintenanceLog(Long taskId, MaintenanceLog log) {
        MaintenanceSchedule task = maintenanceScheduleRepository.findById(taskId)
            .orElseThrow(() -> new MaintenanceException("Maintenance task not found"));

        log.setMaintenanceSchedule(task);
        log.setLoggedAt(LocalDateTime.now());
        
        // Update task status based on log action
        updateTaskStatus(task, log.getAction());
        
        task.getMaintenanceLogs().add(log);
        maintenanceScheduleRepository.save(task);
        
        log.info("Added maintenance log for task: {}", taskId);
        return log;
    }

    public SparePartUsage recordSparePartUsage(Long taskId, SparePartUsage usage) {
        MaintenanceSchedule task = maintenanceScheduleRepository.findById(taskId)
            .orElseThrow(() -> new MaintenanceException("Maintenance task not found"));

        SparePart sparePart = sparePartRepository.findById(usage.getSparePart().getId())
            .orElseThrow(() -> new MaintenanceException("Spare part not found"));

        // Check stock availability
        if (sparePart.getQuantityInStock() < usage.getQuantityUsed()) {
            throw new MaintenanceException("Insufficient spare part quantity in stock");
        }

        // Update stock
        sparePart.setQuantityInStock(sparePart.getQuantityInStock() - usage.getQuantityUsed());
        sparePartRepository.save(sparePart);

        usage.setMaintenanceSchedule(task);
        usage.setUsedAt(LocalDateTime.now());
        usage.setUnitCost(sparePart.getUnitCost());
        
        task.getSparePartsUsed().add(usage);
        maintenanceScheduleRepository.save(task);
        
        log.info("Recorded spare part usage for task: {}", taskId);
        return usage;
    }

    public MaintenanceSchedule assignTechnician(Long taskId, Long technicianId) {
        MaintenanceSchedule task = maintenanceScheduleRepository.findById(taskId)
            .orElseThrow(() -> new MaintenanceException("Maintenance task not found"));

        Employee technician = employeeRepository.findById(technicianId)
            .orElseThrow(() -> new MaintenanceException("Technician not found"));

        task.setAssignedTechnician(technician);
        log.info("Assigned technician {} to task {}", technicianId, taskId);
        return maintenanceScheduleRepository.save(task);
    }

    public MaintenanceSchedule completeMaintenanceTask(Long taskId, String completionNotes) {
        MaintenanceSchedule task = maintenanceScheduleRepository.findById(taskId)
            .orElseThrow(() -> new MaintenanceException("Maintenance task not found"));

        task.setStatus("COMPLETED");
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletionNotes(completionNotes);

        // Calculate actual cost from spare parts used
        task.setActualCost(calculateActualCost(task));
        
        // Create completion log
        MaintenanceLog completionLog = MaintenanceLog.builder()
            .maintenanceSchedule(task)
            .action("COMPLETED")
            .description("Task completed")
            .notes(completionNotes)
            .loggedAt(LocalDateTime.now())
            .technician(task.getAssignedTechnician())
            .build();
        
        task.getMaintenanceLogs().add(completionLog);
        
        // Schedule next maintenance if recurring
        scheduleNextMaintenanceIfNeeded(task);
        
        log.info("Completed maintenance task: {}", taskId);
        return maintenanceScheduleRepository.save(task);
    }

    private void updateTaskStatus(MaintenanceSchedule task, String action) {
        switch (action) {
            case "STARTED" -> task.setStatus("IN_PROGRESS");
            case "COMPLETED" -> task.setStatus("COMPLETED");
            case "PAUSED" -> task.setStatus("PAUSED");
            case "RESUMED" -> task.setStatus("IN_PROGRESS");
        }
    }

    private BigDecimal calculateActualCost(MaintenanceSchedule task) {
        return task.getSparePartsUsed().stream()
            .map(usage -> usage.getUnitCost().multiply(BigDecimal.valueOf(usage.getQuantityUsed())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void scheduleNextMaintenanceIfNeeded(MaintenanceSchedule completedTask) {
        if (completedTask.getRecurrencePattern() == null || completedTask.getRecurrencePattern().equals("NONE")) {
            return;
        }

        LocalDate nextDate = calculateNextMaintenanceDate(
            completedTask.getScheduledDate(),
            completedTask.getRecurrencePattern(),
            completedTask.getRecurrenceInterval()
        );

        MaintenanceSchedule nextTask = MaintenanceSchedule.builder()
            .asset(completedTask.getAsset())
            .taskDescription(completedTask.getTaskDescription())
            .scheduledDate(nextDate)
            .maintenanceType(completedTask.getMaintenanceType())
            .priority(completedTask.getPriority())
            .estimatedCost(completedTask.getEstimatedCost())
            .notificationThreshold(completedTask.getNotificationThreshold())
            .recurrencePattern(completedTask.getRecurrencePattern())
            .recurrenceInterval(completedTask.getRecurrenceInterval())
            .build();

        scheduleMaintenanceTask(nextTask);
        log.info("Scheduled next recurring maintenance task for asset: {}", completedTask.getAsset().getId());
    }

    private LocalDate calculateNextMaintenanceDate(LocalDate currentDate, String pattern, Integer interval) {
        return switch (pattern) {
            case "DAILY" -> currentDate.plusDays(interval);
            case "WEEKLY" -> currentDate.plusWeeks(interval);
            case "MONTHLY" -> currentDate.plusMonths(interval);
            case "YEARLY" -> currentDate.plusYears(interval);
            default -> throw new MaintenanceException("Invalid recurrence pattern");
        };
    }

    public List<MaintenanceSchedule> findUpcomingMaintenanceTasks(int daysAhead) {
        LocalDate endDate = LocalDate.now().plusDays(daysAhead);
        return maintenanceScheduleRepository.findByScheduledDateBetweenAndStatus(
            LocalDate.now(), endDate, "PENDING");
    }

    public List<MaintenanceSchedule> findMaintenanceTasksByTechnician(Long technicianId) {
        return maintenanceScheduleRepository.findByAssignedTechnicianIdAndStatusNot(
            technicianId, "COMPLETED");
    }
} 