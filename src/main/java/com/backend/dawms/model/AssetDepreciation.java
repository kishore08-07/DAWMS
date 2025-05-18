package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetDepreciation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    private String depreciationMethod; // STRAIGHT_LINE, REDUCING_BALANCE
    
    private BigDecimal initialValue;
    private BigDecimal salvageValue;
    private BigDecimal currentValue;
    private BigDecimal annualDepreciationRate;
    private Integer usefulLifeYears;
    
    private LocalDate depreciationStartDate;
    private LocalDate lastCalculationDate;
    
    @OneToMany(mappedBy = "assetDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DepreciationEntry> depreciationHistory = new ArrayList<>();
} 