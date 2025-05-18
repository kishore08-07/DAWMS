package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class MaintenanceTaskDTO {
    private Long taskId;
    private Long assetId;
    private String assetName;
    private String taskDescription;
    private LocalDate scheduledDate;
    private String priority;
    private Long assignedTechnicianId;
    private String assignedTechnicianName;
    private String status;
} 