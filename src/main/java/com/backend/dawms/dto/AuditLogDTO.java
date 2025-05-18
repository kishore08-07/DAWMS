package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogDTO {
    private Long id;
    private LocalDateTime timestamp;
    private String eventType;
    private String eventDescription;
    private String performedBy;
    private Long performedById;
} 