package com.backend.dawms.controller;

import com.backend.dawms.dto.DashboardStatsDTO;
import com.backend.dawms.dto.AlertDTO;
import com.backend.dawms.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertDTO>> getActiveAlerts() {
        return ResponseEntity.ok(dashboardService.getActiveAlerts());
    }
} 