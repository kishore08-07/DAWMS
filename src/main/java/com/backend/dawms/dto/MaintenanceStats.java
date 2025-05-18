package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MaintenanceStats {
    private long scheduledTasks;
    private long overdueTasks;
    private long completedTasks;
    private List<MaintenanceTaskDTO> upcomingTasks;
    private double completionRate;
} 