package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String description;

    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<Asset> assets = new ArrayList<>();
} 