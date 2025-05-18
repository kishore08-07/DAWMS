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
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime timestamp;
    private String eventType;
    private String eventDescription;
    private String entityType;  // Type of entity being audited (e.g., "ASSET", "EMPLOYEE", etc.)
    private String entityId;    // ID of the entity being audited
    
    @ManyToOne
    @JoinColumn(name = "performed_by")
    private Employee performedBy;
    
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;
    
    private String details;
} 