package com.backend.dawms.repository;

import com.backend.dawms.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {
    List<SparePart> findByCategory(String category);
    
    @Query("SELECT s FROM SparePart s WHERE s.quantityInStock <= s.minimumStockLevel")
    List<SparePart> findLowStockParts();
    
    boolean existsByPartNumber(String partNumber);
    
    @Query("SELECT COUNT(s) FROM SparePart s WHERE s.quantityInStock = 0")
    long countOutOfStockParts();
} 