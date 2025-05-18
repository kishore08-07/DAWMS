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
public class MaintenanceSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;
    
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Employee assignedTechnician;
    
    private String taskDescription;
    private LocalDate scheduledDate;
    private String priority;
    private String status;
    
    @Builder.Default
    private Integer notificationThreshold = 7;
    
    @Builder.Default
    private Boolean notificationSent = false;
    
    @OneToMany(mappedBy = "maintenanceSchedule", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MaintenanceLog> maintenanceLogs = new ArrayList<>();
    
    @OneToMany(mappedBy = "maintenanceSchedule", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SparePartUsage> sparePartsUsed = new ArrayList<>();
    
    private String maintenanceType; // PREVENTIVE, CORRECTIVE, etc.
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private LocalDateTime completedAt;
    private String completionNotes;
    private String recurrencePattern; // NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    private Integer recurrenceInterval; // e.g., every 2 weeks, every 3 months
} 