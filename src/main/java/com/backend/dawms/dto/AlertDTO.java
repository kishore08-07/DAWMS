package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AlertDTO {
    private String type;        // "WARRANTY" or "MAINTENANCE"
    private String assetName;
    private String message;
    private LocalDate dueDate;
    private String priority;    // "HIGH", "MEDIUM", "LOW"
} 