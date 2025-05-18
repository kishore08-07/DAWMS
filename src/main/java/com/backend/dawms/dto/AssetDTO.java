package com.backend.dawms.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AssetDTO {
    private Long id;
    private String name;
    private String serialNumber;
    private String category;
    private String status;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String assignedTo;
    private String department;
    private String qrCode;
} 