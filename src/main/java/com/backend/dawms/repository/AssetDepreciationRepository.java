package com.backend.dawms.repository;

import com.backend.dawms.model.AssetDepreciation;
import com.backend.dawms.model.DepreciationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetDepreciationRepository extends JpaRepository<AssetDepreciation, Long> {
    Optional<AssetDepreciation> findByAssetId(Long assetId);
    
    List<AssetDepreciation> findByCurrentValueLessThanEqual(BigDecimal value);
    
    @Query("SELECT d FROM DepreciationEntry d WHERE d.assetDepreciation.asset.id = :assetId " +
           "AND d.entryDate BETWEEN :startDate AND :endDate ORDER BY d.entryDate")
    List<DepreciationEntry> findEntriesByAssetAndDateRange(
        Long assetId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(a.currentValue) FROM AssetDepreciation a")
    BigDecimal calculateTotalCurrentValue();
    
    @Query("SELECT SUM(e.depreciationAmount) FROM AssetDepreciation a JOIN a.depreciationHistory e WHERE e.entryDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalDepreciationForPeriod(LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT ad FROM AssetDepreciation ad JOIN ad.depreciationHistory dh " +
           "WHERE ad.asset.id = :assetId AND dh.entryDate BETWEEN :startDate AND :endDate")
    List<AssetDepreciation> findByAssetIdAndDateBetween(Long assetId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ad FROM AssetDepreciation ad WHERE ad.currentValue <= ad.salvageValue")
    List<AssetDepreciation> getFullyDepreciatedAssets();
} 