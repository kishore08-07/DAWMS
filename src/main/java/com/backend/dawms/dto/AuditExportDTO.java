package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AuditExportDTO {
    private String exportType;
    private String format;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime generatedAt;
    private String status;
    private String exportedBy;
    private List<AuditLogDTO> auditLogs;
} 