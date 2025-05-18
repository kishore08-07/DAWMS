package com.backend.dawms.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AssetStats {
    private long totalAssets;
    private long activeAssets;
    private long inactiveAssets;
    private long availableAssets;
    private long assignedAssets;
    private long maintenanceAssets;
    private BigDecimal totalValue;
    private BigDecimal depreciatedValue;
} 