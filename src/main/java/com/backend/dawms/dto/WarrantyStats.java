package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WarrantyStats {
    private long activeWarranties;
    private long expiringThisMonth;
    private long expiredWarranties;
    private List<WarrantyAlertDTO> upcomingExpirations;
} 