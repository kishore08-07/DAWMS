package com.backend.dawms.service;

import com.backend.dawms.model.AssetDepreciation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing asset depreciation calculations and tracking.
 */
public interface DepreciationService {
    /**
     * Get the total current value of all assets.
     */
    BigDecimal getTotalAssetValue();

    /**
     * Get the total depreciated value (total initial value - total current value).
     */
    BigDecimal getTotalDepreciatedValue();

    /**
     * Calculate and record depreciation for a specific asset.
     */
    void calculateDepreciation(Long assetId);

    /**
     * Calculate total depreciation for an asset within a specific period.
     */
    BigDecimal calculateDepreciationForPeriod(Long assetId, LocalDate startDate, LocalDate endDate);

    /**
     * Update or create depreciation schedule for an asset.
     */
    void updateDepreciationSchedule(Long assetId);

    /**
     * Set up initial depreciation for an asset.
     */
    AssetDepreciation setupDepreciation(Long assetId, AssetDepreciation depreciation);

    /**
     * Generate depreciation report for an asset within a date range.
     */
    List<AssetDepreciation> generateDepreciationReport(Long assetId, LocalDate startDate, LocalDate endDate);

    /**
     * Get list of fully depreciated assets.
     */
    List<AssetDepreciation> getFullyDepreciatedAssets();

    /**
     * Get total depreciation amount for a period across all assets.
     */
    BigDecimal getTotalDepreciationForPeriod(LocalDate startDate, LocalDate endDate);
} 