package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarrantyClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "warranty_id")
    private Warranty warranty;
    
    private String claimNumber;
    private LocalDateTime claimDate;
    private String description;
    private String status; // PENDING, APPROVED, REJECTED, COMPLETED
    private String resolution;
    private LocalDateTime resolutionDate;
    private String remarks;
    
    @Builder.Default
    private boolean notificationSent = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private String createdBy;
    private LocalDateTime lastUpdated;
    private String lastUpdatedBy;
} 