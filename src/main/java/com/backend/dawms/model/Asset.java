package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String serialNumber;
    private String category;
    private String status;
    private String qrCodePath;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee assignedTo;

    private LocalDateTime registrationDate;
    private LocalDateTime lastUpdated;

    private String type;
    private String description;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    
    private BigDecimal purchasePrice;
    private BigDecimal currentValue;
    private BigDecimal salvageValue;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Warranty> warranties = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaintenanceSchedule> maintenanceSchedules = new ArrayList<>();

    private Integer usefulLifeYears;
}
