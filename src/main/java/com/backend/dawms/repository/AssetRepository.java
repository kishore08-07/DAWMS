package com.backend.dawms.repository;

import com.backend.dawms.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.math.BigDecimal;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    long countByStatus(String status);
    
    @Query("SELECT COALESCE(SUM(a.purchasePrice), 0) FROM Asset a")
    BigDecimal calculateTotalAssetValue();
    
    @Query("SELECT COALESCE(SUM(a.currentValue), 0) FROM Asset a")
    BigDecimal calculateTotalCurrentValue();
}
