package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ComplianceAlertDTO {
    private Long recordId;
    private Long assetId;
    private String assetName;
    private String complianceType;
    private LocalDateTime nextReviewDate;
    private String status;
    private String priority;
} 