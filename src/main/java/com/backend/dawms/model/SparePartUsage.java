package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparePartUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "maintenance_schedule_id")
    private MaintenanceSchedule maintenanceSchedule;
    
    @ManyToOne
    @JoinColumn(name = "spare_part_id")
    private SparePart sparePart;
    
    private Integer quantityUsed;
    private BigDecimal unitCost;
    private LocalDateTime usedAt;
    private String notes;
    
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Employee technician;
} 