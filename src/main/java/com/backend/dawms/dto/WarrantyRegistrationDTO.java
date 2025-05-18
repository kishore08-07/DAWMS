package com.backend.dawms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WarrantyRegistrationDTO {
    private Long assetId;
    private String warrantyNumber;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String vendorName;
    private String vendorEmail;
    private String vendorPhone;
    private String warrantyTerms;
} 