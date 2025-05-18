package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ComplianceStats {
    private long totalRecords;
    private long pendingReviews;
    private long expiringCertificates;
    private long overdueChecks;
    private List<ComplianceAlertDTO> upcomingDeadlines;
    private double complianceRate;
} 