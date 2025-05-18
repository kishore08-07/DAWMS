package com.backend.dawms.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.backend.dawms.model.Asset;
import com.backend.dawms.model.Department;
import com.backend.dawms.model.Employee;
import com.backend.dawms.repository.AssetRepository;
import com.backend.dawms.repository.DepartmentRepository;
import com.backend.dawms.repository.EmployeeRepository;
import com.backend.dawms.dto.AssetRegistrationDTO;
import com.backend.dawms.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final QRCodeService qrCodeService;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Asset getAsset(Long id) {
        return assetRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Asset not found with id: " + id));
    }

    public Asset create(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset update(Long id, Asset assetDetails) {
        Asset asset = getAsset(id);

        asset.setName(assetDetails.getName());
        asset.setType(assetDetails.getType());
        asset.setDescription(assetDetails.getDescription());
        asset.setAssignedTo(assetDetails.getAssignedTo());
        asset.setPurchaseDate(assetDetails.getPurchaseDate());
        asset.setWarrantyExpiry(assetDetails.getWarrantyExpiry());

        return assetRepository.save(asset);
    }

    @Transactional
    public void deleteAsset(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new EntityNotFoundException("Asset not found with id: " + id);
        }
        assetRepository.deleteById(id);
    }

    @Transactional
    public Asset registerAsset(AssetRegistrationDTO dto) {
        Department department = departmentRepository.findById(dto.getDepartmentId())
            .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        Employee assignedTo = null;
        if (dto.getAssignedEmployeeId() != null) {
            assignedTo = employeeRepository.findById(dto.getAssignedEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        }

        Asset asset = Asset.builder()
            .name(dto.getName())
            .serialNumber(dto.getSerialNumber())
            .category(dto.getCategory())
            .type(dto.getType())
            .description(dto.getDescription())
            .status("ACTIVE")
            .department(department)
            .assignedTo(assignedTo)
            .registrationDate(LocalDateTime.now())
            .lastUpdated(LocalDateTime.now())
            .purchaseDate(dto.getPurchaseDate())
            .warrantyExpiry(dto.getWarrantyExpiry())
            .build();

        asset = assetRepository.save(asset);

        try {
            String qrCodePath = qrCodeService.generateQRCode(asset);
            asset.setQrCodePath(qrCodePath);
            return assetRepository.save(asset);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code for asset", e);
        }
    }

    public Asset findById(Long id) {
        return assetRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Asset not found with id: " + id));
    }

}
