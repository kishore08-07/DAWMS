package com.backend.dawms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Asset asset;
    private LocalDate scheduledDate;
    private String taskDescription;
    private String assignedTechnician;
    private String status; // SCHEDULED, COMPLETED, PENDING
    private BigDecimal cost;
    private String notes;
} 