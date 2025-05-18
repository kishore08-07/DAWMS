package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warranty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;
    
    private String warrantyNumber;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String vendorName;
    private String vendorEmail;
    private String vendorPhone;
    private String warrantyTerms;
    private String status; // ACTIVE, EXPIRED, CLAIMED
    
    @Builder.Default
    private Boolean expiryNotificationSent = false;
    
    @Builder.Default
    private Integer notificationThreshold = 30; // Days before expiry to send notification
    
    @OneToMany(mappedBy = "warranty", cascade = CascadeType.ALL)
    @Builder.Default
    private List<WarrantyClaim> claims = new ArrayList<>();

    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
} 