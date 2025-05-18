package com.backend.dawms.service;

import com.backend.dawms.dto.WarrantyRegistrationDTO;
import com.backend.dawms.exception.WarrantyException;
import com.backend.dawms.model.Asset;
import com.backend.dawms.model.Warranty;
import com.backend.dawms.model.WarrantyClaim;

import com.backend.dawms.repository.AssetRepository;
import com.backend.dawms.repository.WarrantyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarrantyService {
    private final WarrantyRepository warrantyRepository;
    private final AssetRepository assetRepository;

    public Warranty registerWarranty(WarrantyRegistrationDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId())
            .orElseThrow(() -> new WarrantyException("Asset not found with ID: " + dto.getAssetId()));

        if (dto.getExpiryDate().isBefore(dto.getPurchaseDate())) {
            throw new WarrantyException("Warranty expiry date cannot be before purchase date");
        }

        Warranty warranty = Warranty.builder()
            .asset(asset)
            .warrantyNumber(dto.getWarrantyNumber())
            .purchaseDate(dto.getPurchaseDate())
            .expiryDate(dto.getExpiryDate())
            .vendorName(dto.getVendorName())
            .vendorEmail(dto.getVendorEmail())
            .vendorPhone(dto.getVendorPhone())
            .warrantyTerms(dto.getWarrantyTerms())
            .status("ACTIVE")
            .expiryNotificationSent(false)
            .claims(new ArrayList<>())
            .build();

        log.info("Registering new warranty for asset: {}", asset.getId());
        return warrantyRepository.save(warranty);
    }

    public WarrantyClaim createClaim(Long warrantyId, String description) {
        Warranty warranty = warrantyRepository.findById(warrantyId)
            .orElseThrow(() -> new WarrantyException("Warranty not found with ID: " + warrantyId));

        if ("EXPIRED".equals(warranty.getStatus())) {
            throw new WarrantyException("Cannot create claim for expired warranty");
        }

        WarrantyClaim claim = WarrantyClaim.builder()
            .warranty(warranty)
            .claimDate(LocalDateTime.now())
            .claimNumber("CLM-" + System.currentTimeMillis())
            .description(description)
            .status("PENDING")
            .build();

        warranty.getClaims().add(claim);
        warrantyRepository.save(warranty);
        log.info("Created new warranty claim for warranty: {}", warrantyId);
        return claim;
    }

    public List<Warranty> findExpiringWarranties(int daysThreshold) {
        if (daysThreshold <= 0) {
            throw new WarrantyException("Days threshold must be greater than 0");
        }
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return warrantyRepository.findByExpiryDateBeforeAndStatus(thresholdDate, "ACTIVE");
    }

    @Transactional
    public void updateWarrantyStatus() {
        List<Warranty> expiredWarranties = warrantyRepository
            .findByExpiryDateBeforeAndStatus(LocalDate.now(), "ACTIVE");
        
        for (Warranty warranty : expiredWarranties) {
            warranty.setStatus("EXPIRED");
            warrantyRepository.save(warranty);
            log.info("Updated warranty {} status to EXPIRED", warranty.getId());
        }
    }
} 