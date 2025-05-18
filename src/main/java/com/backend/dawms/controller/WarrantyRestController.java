package com.backend.dawms.controller;

import com.backend.dawms.dto.WarrantyRegistrationDTO;
import com.backend.dawms.model.Warranty;
import com.backend.dawms.model.WarrantyClaim;
import com.backend.dawms.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warranties")
@Slf4j
@RequiredArgsConstructor
public class WarrantyRestController {
    private final WarrantyService warrantyService;

    @PostMapping("/register")
    public ResponseEntity<Warranty> registerWarranty(@RequestBody WarrantyRegistrationDTO dto) {
        try {
            Warranty warranty = warrantyService.registerWarranty(dto);
            return ResponseEntity.ok(warranty);
        } catch (Exception e) {
            log.error("Error registering warranty", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{warrantyId}/claims")
    public ResponseEntity<WarrantyClaim> createClaim(
            @PathVariable Long warrantyId,
            @RequestBody String description) {
        try {
            WarrantyClaim claim = warrantyService.createClaim(warrantyId, description);
            return ResponseEntity.ok(claim);
        } catch (Exception e) {
            log.error("Error creating warranty claim", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Warranty>> getExpiringWarranties(
            @RequestParam(defaultValue = "30") int daysThreshold) {
        List<Warranty> warranties = warrantyService.findExpiringWarranties(daysThreshold);
        return ResponseEntity.ok(warranties);
    }
} 