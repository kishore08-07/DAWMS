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
public class ComplianceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;
    
    private String complianceType; // SAFETY, REGULATORY, ENVIRONMENTAL, etc.
    private LocalDateTime checkDate;
    private LocalDateTime nextReviewDate;
    private String compliancePriority; // HIGH, MEDIUM, LOW
    private String status; // COMPLIANT, NON_COMPLIANT, PENDING_REVIEW
    private String findings;
    private String recommendations;
    private String actionTaken;
    
    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private Employee reviewedBy;
    private LocalDateTime reviewDate;
    
    @Builder.Default
    private boolean notificationSent = false;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private String createdBy;
    private LocalDateTime lastUpdated;
    private String lastUpdatedBy;
    
    private String attachments; // URLs or file paths
    private String notes;
} 