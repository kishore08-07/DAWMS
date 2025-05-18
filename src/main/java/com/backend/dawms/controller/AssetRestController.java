package com.backend.dawms.controller;

import com.backend.dawms.dto.AssetRegistrationDTO;
import com.backend.dawms.model.Asset;
import com.backend.dawms.service.AssetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Slf4j
@RequiredArgsConstructor
public class AssetRestController {
    private final AssetService assetService;

    @PostMapping("/register")
    public ResponseEntity<Asset> registerAsset(@RequestBody AssetRegistrationDTO assetDTO) {
        try {
            Asset asset = assetService.registerAsset(assetDTO);
            return ResponseEntity.ok(asset);
        } catch (Exception e) {
            log.error("Error registering asset", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAsset(@PathVariable Long id) {
        try {
            Asset asset = assetService.findById(id);
            return ResponseEntity.ok(asset);
        } catch (EntityNotFoundException e) {
            log.error("Asset not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching asset with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        try {
            List<Asset> assets = assetService.getAllAssets();
            return ResponseEntity.ok(assets);
        } catch (Exception e) {
            log.error("Error fetching all assets", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset assetDetails) {
        try {
            Asset updatedAsset = assetService.update(id, assetDetails);
            return ResponseEntity.ok(updatedAsset);
        } catch (EntityNotFoundException e) {
            log.error("Asset not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating asset with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        try {
            assetService.deleteAsset(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            log.error("Asset not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting asset with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 