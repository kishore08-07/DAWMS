package com.backend.dawms.controller;

import com.backend.dawms.model.AssetDepreciation;
import com.backend.dawms.service.DepreciationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/depreciation")
@RequiredArgsConstructor
public class DepreciationController {
    private final DepreciationService depreciationService;

    @PostMapping("/{assetId}/setup")
    public ResponseEntity<AssetDepreciation> setupDepreciation(
            @PathVariable Long assetId,
            @Valid @RequestBody AssetDepreciation depreciation) {
        return ResponseEntity.ok(depreciationService.setupDepreciation(assetId, depreciation));
    }

    @GetMapping("/{assetId}/report")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN', 'TECHNICIAN')")
    public ResponseEntity<List<AssetDepreciation>> generateDepreciationReport(
            @PathVariable Long assetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(depreciationService.generateDepreciationReport(assetId, startDate, endDate));
    }

    @GetMapping("/fully-depreciated")
    public ResponseEntity<List<AssetDepreciation>> getFullyDepreciatedAssets() {
        return ResponseEntity.ok(depreciationService.getFullyDepreciatedAssets());
    }

    @GetMapping("/period-total")
    public ResponseEntity<BigDecimal> getTotalDepreciationForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(depreciationService.getTotalDepreciationForPeriod(startDate, endDate));
    }
} 