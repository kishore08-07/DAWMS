package com.backend.dawms.repository;

import com.backend.dawms.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {
    List<Warranty> findByAssetId(Long assetId);
    
    @Query("SELECT COUNT(w) FROM Warranty w WHERE w.status = 'ACTIVE'")
    long countActiveWarranties();
    
    @Query("SELECT COUNT(w) FROM Warranty w WHERE w.status = 'ACTIVE' AND w.expiryDate < CURRENT_DATE")
    long countExpiredWarranties();
    
    long countByExpiryDateAfter(LocalDate date);
    
    long countByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT w FROM Warranty w WHERE w.expiryDate BETWEEN :startDate AND :endDate AND w.expiryNotificationSent = false")
    List<Warranty> findUpcomingExpirations(LocalDate startDate, LocalDate endDate);

    List<Warranty> findByExpiryDateBeforeAndStatus(LocalDate date, String status);
}