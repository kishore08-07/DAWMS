package com.backend.dawms.controller;

import com.backend.dawms.model.Warranty;
import com.backend.dawms.repository.AssetRepository;
import com.backend.dawms.repository.WarrantyRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warranties")
@RequiredArgsConstructor
@Slf4j
public class WarrantyController {

    private final WarrantyRepository warrantyRepository;
    private final AssetRepository assetRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'TECHNICIAN')")
    public ResponseEntity<List<Warranty>> getAllWarranties() {
        try {
            log.debug("Fetching all warranties");
            List<Warranty> warranties = warrantyRepository.findAll();
            return ResponseEntity.ok(warranties);
        } catch (DataAccessException e) {
            log.error("Database error while fetching warranties: ", e);
            throw new RuntimeException("Error accessing warranty data", e);
        } catch (Exception e) {
            log.error("Error fetching warranties: ", e);
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'TECHNICIAN')")
    public ResponseEntity<Warranty> getWarrantyById(@PathVariable Long id) {
        try {
            log.debug("Fetching warranty with id: {}", id);
            return warrantyRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching warranty with id {}: ", id, e);
            throw e;
        }
    }
    
    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'TECHNICIAN')")
    public ResponseEntity<List<Warranty>> getWarrantiesByAssetId(@PathVariable Long assetId) {
        try {
            log.debug("Fetching warranties for asset id: {}", assetId);
            List<Warranty> warranties = warrantyRepository.findByAssetId(assetId);
            return ResponseEntity.ok(warranties);
        } catch (Exception e) {
            log.error("Error fetching warranties for asset {}: ", assetId, e);
            throw e;
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<Warranty> createWarranty(@Valid @RequestBody Warranty warranty) {
        // Ensure asset exists
        return assetRepository.findById(warranty.getAsset().getId())
                .map(asset -> {
                    warranty.setAsset(asset);
                    Warranty savedWarranty = warrantyRepository.save(warranty);
                    return ResponseEntity.ok(savedWarranty);
                })
                .orElse(ResponseEntity.badRequest().build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<Warranty> updateWarranty(@PathVariable Long id, @Valid @RequestBody Warranty warrantyDetails) {
        return warrantyRepository.findById(id)
                .map(existingWarranty -> {
                    // Update fields but preserve the original asset relationship
                    existingWarranty.setWarrantyNumber(warrantyDetails.getWarrantyNumber());
                    existingWarranty.setPurchaseDate(warrantyDetails.getPurchaseDate());
                    existingWarranty.setExpiryDate(warrantyDetails.getExpiryDate());
                    existingWarranty.setVendorName(warrantyDetails.getVendorName());
                    existingWarranty.setVendorEmail(warrantyDetails.getVendorEmail());
                    existingWarranty.setVendorPhone(warrantyDetails.getVendorPhone());
                    existingWarranty.setWarrantyTerms(warrantyDetails.getWarrantyTerms());
                    existingWarranty.setNotificationThreshold(warrantyDetails.getNotificationThreshold());
                    
                    // Reset notification flag if expiry date changed
                    if (warrantyDetails.getExpiryDate() != null && 
                            !warrantyDetails.getExpiryDate().equals(existingWarranty.getExpiryDate())) {
                        existingWarranty.setExpiryNotificationSent(false);
                    }
                    
                    Warranty updatedWarranty = warrantyRepository.save(existingWarranty);
                    return ResponseEntity.ok(updatedWarranty);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWarranty(@PathVariable Long id) {
        return warrantyRepository.findById(id)
                .map(warranty -> {
                    warrantyRepository.delete(warranty);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Access denied");
        response.put("message", "You don't have permission to access this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Unhandled exception in WarrantyController: ", e);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}