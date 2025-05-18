package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String partNumber;
    private String description;
    private String manufacturer;
    private Integer quantityInStock;
    private Integer minimumStockLevel;
    private BigDecimal unitCost;
    private String location; // Storage location
    private String category;
    
    @OneToMany(mappedBy = "sparePart")
    private List<SparePartUsage> usages;
} 