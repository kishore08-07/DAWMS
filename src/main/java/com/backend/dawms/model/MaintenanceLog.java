package com.backend.dawms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "maintenance_schedule_id")
    private MaintenanceSchedule maintenanceSchedule;
    
    private LocalDateTime loggedAt;
    private String action; // STARTED, PAUSED, RESUMED, COMPLETED, NOTE_ADDED
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Employee technician;
    
    private String notes;

    public static MaintenanceLog info(String message, Long technicianId) {
        return MaintenanceLog.builder()
            .action("NOTE_ADDED")
            .description(message)
            .loggedAt(LocalDateTime.now())
            .technician(Employee.builder().id(technicianId).build())
            .build();
    }
} 