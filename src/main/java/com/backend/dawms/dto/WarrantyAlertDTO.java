package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class WarrantyAlertDTO {
    private Long warrantyId;
    private Long assetId;
    private String assetName;
    private LocalDate expiryDate;
    private long daysUntilExpiry;
    private String status;
} 