package com.backend.dawms.controller;

import com.backend.dawms.model.Asset;
import com.backend.dawms.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {
    @Autowired
    private AssetService assetService;

    @GetMapping
    public ResponseEntity< List<Asset> > getAllAssets() {
        List <Asset> asset=assetService.getAllAssets();
        return ResponseEntity.status(200).body(asset);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAsset(@PathVariable int id){
        Asset asset=assetService.getAsset(id);
        if(asset!=null){
            return ResponseEntity.ok().body(asset);
        }
        return ResponseEntity.status(404).body(null);
    }

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        Asset created=assetService.create(asset);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable int id,@RequestBody Asset assetDetails) {
        Asset updated=assetService.update(id,assetDetails);
        return ResponseEntity.status(201).body(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteAsset(@PathVariable int id) {
        assetService.delete(id);
        return "Asset deleted successfully";
    }


}
