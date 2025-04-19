package com.backend.dawms.service;

import java.util.List;
import java.util.Optional;

import com.backend.dawms.model.Asset;
import com.backend.dawms.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getAsset(int id) {
        return assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found!"));
    }

    public Asset create(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset update(int id, Asset assetDetails) {
        Asset asset= getAsset(id);

        asset.setName(assetDetails.getName());
        asset.setType(assetDetails.getType());
        asset.setDescription(assetDetails.getDescription());
        asset.setAssignedTo(assetDetails.getAssignedTo());
        asset.setPurchaseDate(assetDetails.getPurchaseDate());
        asset.setWarrantyExpiry(assetDetails.getWarrantyExpiry());

        return assetRepository.save(asset);
    }

    public void delete(int id) {
        assetRepository.deleteById(id);
    }

}
