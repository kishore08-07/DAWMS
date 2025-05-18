package com.backend.dawms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AssetRegistrationDTO {
    private String name;
    private String serialNumber;
    private String category;
    private String type;
    private String description;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    private Long departmentId;
    private Long assignedEmployeeId;
} 