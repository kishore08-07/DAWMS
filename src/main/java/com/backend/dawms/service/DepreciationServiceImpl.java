//package com.backend.dawms.service;
//
//import com.backend.dawms.model.Asset;
//import com.backend.dawms.model.AssetDepreciation;
//import com.backend.dawms.model.DepreciationEntry;
//import com.backend.dawms.repository.AssetDepreciationRepository;
//import com.backend.dawms.repository.AssetRepository;
//import com.backend.dawms.exception.ResourceNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class DepreciationServiceImpl implements DepreciationService {
//    private final AssetRepository assetRepository;
//    private final AssetDepreciationRepository depreciationRepository;
//
//    @Override
//    public BigDecimal getTotalAssetValue() {
//        return BigDecimal.valueOf(Optional.ofNullable(assetRepository.calculateTotalAssetValue())
//            .orElse(BigDecimal.ZERO));
//    }
//
//    @Override
//    private BigDecimal getTotalDepreciatedValue() {
//        BigDecimal totalAssetValue = Optional.ofNullable(depreciationRepository.calculateTotalCurrentValue())
//                .orElse(BigDecimal.ZERO);
//        BigDecimal totalInitialValue = Optional.ofNullable(assetRepository.calculateTotalAssetValue())
//                .orElse(BigDecimal.ZERO);
//        return totalInitialValue.subtract(totalAssetValue).setScale(2, RoundingMode.HALF_UP);
//    }
//
//    @Override
//    @Transactional
//    public void calculateDepreciation(Long assetId) {
//        AssetDepreciation depreciation = depreciationRepository.findByAssetId(assetId)
//            .orElseThrow(() -> new ResourceNotFoundException("AssetDepreciation", "assetId", assetId));
//
//        LocalDate today = LocalDate.now();
//        if (depreciation.getLastCalculationDate().isBefore(today)) {
//            BigDecimal depreciationAmount = calculateDailyDepreciation(depreciation);
//            updateAssetValue(depreciation, depreciationAmount);
//            recordDepreciationEntry(depreciation, depreciationAmount);
//            depreciation.setLastCalculationDate(today);
//            depreciationRepository.save(depreciation);
//        }
//    }
//
//    @Override
//    public BigDecimal calculateDepreciationForPeriod(Long assetId, LocalDate startDate, LocalDate endDate) {
//        AssetDepreciation depreciation = depreciationRepository.findByAssetId(assetId)
//            .orElseThrow(() -> new ResourceNotFoundException("AssetDepreciation", "assetId", assetId));
//
//        return depreciation.getDepreciationHistory().stream()
//            .filter(entry -> !entry.getEntryDate().isBefore(startDate) && !entry.getEntryDate().isAfter(endDate))
//            .map(DepreciationEntry::getDepreciationAmount)
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    @Override
//    @Transactional
//    public void updateDepreciationSchedule(Long assetId) {
//        Asset asset = assetRepository.findById(assetId)
//            .orElseThrow(() -> new ResourceNotFoundException("Asset", "id", assetId));
//
//        AssetDepreciation depreciation = depreciationRepository.findByAssetId(assetId)
//            .orElseGet(() -> createNewDepreciationSchedule(asset));
//
//        calculateDepreciation(assetId);
//    }
//
//    private AssetDepreciation createNewDepreciationSchedule(Asset asset) {
//        BigDecimal salvageValue = calculateSalvageValue(asset);
//        BigDecimal annualRate = calculateAnnualDepreciationRate(asset.getPurchasePrice(), salvageValue, asset.getUsefulLifeYears());
//
//        AssetDepreciation depreciation = AssetDepreciation.builder()
//            .asset(asset)
//            .initialValue(asset.getPurchasePrice())
//            .currentValue(asset.getPurchasePrice())
//            .salvageValue(salvageValue)
//            .usefulLifeYears(asset.getUsefulLifeYears())
//            .depreciationStartDate(asset.getPurchaseDate())
//            .lastCalculationDate(asset.getPurchaseDate())
//            .depreciationMethod("STRAIGHT_LINE")
//            .annualDepreciationRate(annualRate)
//            .build();
//
//        return depreciationRepository.save(depreciation);
//    }
//
//    private BigDecimal calculateSalvageValue(Asset asset) {
//        if (asset.getSalvageValue() != null) {
//            return asset.getSalvageValue();
//        }
//
//        if (asset.getPurchasePrice() == null) {
//            return BigDecimal.ZERO;
//        }
//
//        return asset.getPurchasePrice()
//            .multiply(new BigDecimal("0.10"))
//            .setScale(2, RoundingMode.HALF_UP);
//    }
//
//    private BigDecimal calculateAnnualDepreciationRate(BigDecimal initialValue, BigDecimal salvageValue, int usefulLifeYears) {
//        if (initialValue == null || salvageValue == null || usefulLifeYears <= 0) {
//            return BigDecimal.ZERO;
//        }
//
//        return initialValue.subtract(salvageValue)
//            .divide(BigDecimal.valueOf(usefulLifeYears), 2, RoundingMode.HALF_UP);
//    }
//
//    private BigDecimal calculateDailyDepreciation(AssetDepreciation depreciation) {
//        if (depreciation.getInitialValue() == null ||
//            depreciation.getSalvageValue() == null ||
//            depreciation.getUsefulLifeYears() == null ||
//            depreciation.getUsefulLifeYears() <= 0) {
//            return BigDecimal.ZERO;
//        }
//
//        BigDecimal totalDepreciation = depreciation.getInitialValue().subtract(depreciation.getSalvageValue());
//        long totalDays = ChronoUnit.DAYS.between(
//            depreciation.getDepreciationStartDate(),
//            depreciation.getDepreciationStartDate().plusYears(depreciation.getUsefulLifeYears())
//        );
//
//        return totalDays > 0
//            ? totalDepreciation.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
//            : BigDecimal.ZERO;
//    }
//
//    private void updateAssetValue(AssetDepreciation depreciation, BigDecimal dailyDepreciation) {
//        if (depreciation.getCurrentValue() == null ||
//            depreciation.getSalvageValue() == null ||
//            dailyDepreciation == null) {
//            return;
//        }
//
//        BigDecimal newValue = depreciation.getCurrentValue().subtract(dailyDepreciation);
//        if (newValue.compareTo(depreciation.getSalvageValue()) < 0) {
//            newValue = depreciation.getSalvageValue();
//        }
//        depreciation.setCurrentValue(newValue);
//    }
//
//    private void recordDepreciationEntry(AssetDepreciation depreciation, BigDecimal amount) {
//        if (amount == null || depreciation.getCurrentValue() == null) {
//            return;
//        }
//
//        DepreciationEntry entry = DepreciationEntry.builder()
//            .assetDepreciation(depreciation)
//            .entryDate(LocalDate.now())
//            .depreciationAmount(amount)
//            .currentValue(depreciation.getCurrentValue())
//            .build();
//        depreciation.getDepreciationHistory().add(entry);
//    }
//
//    @Override
//    public AssetDepreciation setupDepreciation(Long assetId, AssetDepreciation depreciation) {
//        Asset asset = assetRepository.findById(assetId)
//            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));
//
//        BigDecimal salvageValue = calculateSalvageValue(asset);
//        BigDecimal annualRate = calculateAnnualDepreciationRate(asset.getPurchasePrice(), salvageValue, asset.getUsefulLifeYears());
//
//        depreciation.setAsset(asset);
//        depreciation.setInitialValue(asset.getPurchasePrice());
//        depreciation.setCurrentValue(asset.getPurchasePrice());
//        depreciation.setSalvageValue(salvageValue);
//        depreciation.setUsefulLifeYears(asset.getUsefulLifeYears());
//        depreciation.setDepreciationStartDate(asset.getPurchaseDate());
//        depreciation.setLastCalculationDate(asset.getPurchaseDate());
//        depreciation.setDepreciationMethod("STRAIGHT_LINE");
//        depreciation.setAnnualDepreciationRate(annualRate);
//
//        return depreciationRepository.save(depreciation);
//    }
//
//    @Override
//    public List<AssetDepreciation> generateDepreciationReport(Long assetId, LocalDate startDate, LocalDate endDate) {
//        return depreciationRepository.findByAssetIdAndDateBetween(assetId, startDate, endDate);
//    }
//
//    @Override
//    public List<AssetDepreciation> getFullyDepreciatedAssets() {
//        return depreciationRepository.findByCurrentValueLessThanEqual(BigDecimal.ZERO);
//    }
//
//    @Override
//    public BigDecimal getTotalDepreciationForPeriod(LocalDate startDate, LocalDate endDate) {
//        return depreciationRepository.calculateTotalDepreciationForPeriod(startDate, endDate);
//    }
//}
package com.backend.dawms.service;

import com.backend.dawms.model.Asset;
import com.backend.dawms.model.AssetDepreciation;
import com.backend.dawms.model.DepreciationEntry;
import com.backend.dawms.repository.AssetDepreciationRepository;
import com.backend.dawms.repository.AssetRepository;
import com.backend.dawms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepreciationServiceImpl implements DepreciationService {
    private final AssetRepository assetRepository;
    private final AssetDepreciationRepository depreciationRepository;

    @Override
    public BigDecimal getTotalAssetValue() {
        return Optional.ofNullable(assetRepository.calculateTotalAssetValue())
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalDepreciatedValue() {
        BigDecimal totalCurrentValue = Optional.ofNullable(depreciationRepository.calculateTotalCurrentValue())
                .orElse(BigDecimal.ZERO);
        BigDecimal totalInitialValue = Optional.ofNullable(assetRepository.calculateTotalAssetValue())
                .orElse(BigDecimal.ZERO);

        return totalInitialValue.subtract(totalCurrentValue).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void calculateDepreciation(Long assetId) {
        AssetDepreciation depreciation = depreciationRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("AssetDepreciation", "assetId", assetId));

        LocalDate today = LocalDate.now();
        if (depreciation.getLastCalculationDate().isBefore(today)) {
            BigDecimal dailyAmount = calculateDailyDepreciation(depreciation);
            updateAssetValue(depreciation, dailyAmount);
            recordDepreciationEntry(depreciation, dailyAmount);
            depreciation.setLastCalculationDate(today);
            depreciationRepository.save(depreciation);
        }
    }

    @Override
    public BigDecimal calculateDepreciationForPeriod(Long assetId, LocalDate startDate, LocalDate endDate) {
        AssetDepreciation depreciation = depreciationRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("AssetDepreciation", "assetId", assetId));

        return depreciation.getDepreciationHistory().stream()
                .filter(entry -> !entry.getEntryDate().isBefore(startDate) && !entry.getEntryDate().isAfter(endDate))
                .map(DepreciationEntry::getDepreciationAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public void updateDepreciationSchedule(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "id", assetId));

        depreciationRepository.findByAssetId(assetId)
                .orElseGet(() -> createNewDepreciationSchedule(asset));

        calculateDepreciation(assetId);
    }

    private AssetDepreciation createNewDepreciationSchedule(Asset asset) {
        BigDecimal salvageValue = calculateSalvageValue(asset);
        BigDecimal annualRate = calculateAnnualDepreciationRate(asset.getPurchasePrice(), salvageValue, asset.getUsefulLifeYears());

        AssetDepreciation depreciation = AssetDepreciation.builder()
                .asset(asset)
                .initialValue(asset.getPurchasePrice())
                .currentValue(asset.getPurchasePrice())
                .salvageValue(salvageValue)
                .usefulLifeYears(asset.getUsefulLifeYears())
                .depreciationStartDate(asset.getPurchaseDate())
                .lastCalculationDate(asset.getPurchaseDate())
                .depreciationMethod("STRAIGHT_LINE")
                .annualDepreciationRate(annualRate)
                .build();

        return depreciationRepository.save(depreciation);
    }

    private BigDecimal calculateSalvageValue(Asset asset) {
        if (asset.getSalvageValue() != null) {
            return asset.getSalvageValue();
        }
        if (asset.getPurchasePrice() == null) {
            return BigDecimal.ZERO;
        }
        return asset.getPurchasePrice()
                .multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAnnualDepreciationRate(BigDecimal initialValue, BigDecimal salvageValue, int usefulLifeYears) {
        if (initialValue == null || salvageValue == null || usefulLifeYears <= 0) {
            return BigDecimal.ZERO;
        }
        return initialValue.subtract(salvageValue)
                .divide(BigDecimal.valueOf(usefulLifeYears), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDailyDepreciation(AssetDepreciation depreciation) {
        if (depreciation.getInitialValue() == null ||
                depreciation.getSalvageValue() == null ||
                depreciation.getUsefulLifeYears() == null ||
                depreciation.getUsefulLifeYears() <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDepreciation = depreciation.getInitialValue().subtract(depreciation.getSalvageValue());
        long totalDays = ChronoUnit.DAYS.between(
                depreciation.getDepreciationStartDate(),
                depreciation.getDepreciationStartDate().plusYears(depreciation.getUsefulLifeYears())
        );

        return totalDays > 0
                ? totalDepreciation.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    private void updateAssetValue(AssetDepreciation depreciation, BigDecimal dailyDepreciation) {
        if (depreciation.getCurrentValue() == null ||
                depreciation.getSalvageValue() == null ||
                dailyDepreciation == null) {
            return;
        }

        BigDecimal newValue = depreciation.getCurrentValue().subtract(dailyDepreciation);
        if (newValue.compareTo(depreciation.getSalvageValue()) < 0) {
            newValue = depreciation.getSalvageValue();
        }
        depreciation.setCurrentValue(newValue);
    }

    private void recordDepreciationEntry(AssetDepreciation depreciation, BigDecimal amount) {
        if (amount == null || depreciation.getCurrentValue() == null) {
            return;
        }

        DepreciationEntry entry = DepreciationEntry.builder()
                .assetDepreciation(depreciation)
                .entryDate(LocalDate.now())
                .depreciationAmount(amount)
                .currentValue(depreciation.getCurrentValue())
                .build();

        depreciation.getDepreciationHistory().add(entry);
    }

    @Override
    public AssetDepreciation setupDepreciation(Long assetId, AssetDepreciation depreciation) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        BigDecimal salvageValue = calculateSalvageValue(asset);
        BigDecimal annualRate = calculateAnnualDepreciationRate(asset.getPurchasePrice(), salvageValue, asset.getUsefulLifeYears());

        depreciation.setAsset(asset);
        depreciation.setInitialValue(asset.getPurchasePrice());
        depreciation.setCurrentValue(asset.getPurchasePrice());
        depreciation.setSalvageValue(salvageValue);
        depreciation.setUsefulLifeYears(asset.getUsefulLifeYears());
        depreciation.setDepreciationStartDate(asset.getPurchaseDate());
        depreciation.setLastCalculationDate(asset.getPurchaseDate());
        depreciation.setDepreciationMethod("STRAIGHT_LINE");
        depreciation.setAnnualDepreciationRate(annualRate);

        return depreciationRepository.save(depreciation);
    }

    @Override
    public List<AssetDepreciation> generateDepreciationReport(Long assetId, LocalDate startDate, LocalDate endDate) {
        return depreciationRepository.findByAssetIdAndDateBetween(assetId, startDate, endDate);
    }

    @Override
    public List<AssetDepreciation> getFullyDepreciatedAssets() {
        return depreciationRepository.findByCurrentValueLessThanEqual(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTotalDepreciationForPeriod(LocalDate startDate, LocalDate endDate) {
        return Optional.ofNullable(depreciationRepository.calculateTotalDepreciationForPeriod(startDate, endDate))
                .orElse(BigDecimal.ZERO);
    }
}
