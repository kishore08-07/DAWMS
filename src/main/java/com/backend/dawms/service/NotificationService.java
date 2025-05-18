package com.backend.dawms.service;

import com.backend.dawms.model.Employee;
import com.backend.dawms.model.MaintenanceSchedule;
import com.backend.dawms.model.Warranty;
import com.backend.dawms.repository.MaintenanceScheduleRepository;
import com.backend.dawms.repository.WarrantyRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final WarrantyRepository warrantyRepository;
    private final MaintenanceScheduleRepository maintenanceRepository;
    private final EmailService emailService;
    private final JavaMailSender emailSender;

    @Scheduled(cron = "0 0 8 * * ?") // Run all checks at 8 AM
    @Transactional
    public void checkWarrantyAndMaintenanceStatus() {
        log.info("Starting daily warranty and maintenance checks");
        checkWarrantyExpirations();
        checkUpcomingMaintenance();
        updateOverdueTasks();
        log.info("Completed daily warranty and maintenance checks");
    }

    private void checkWarrantyExpirations() {
        log.info("Checking warranty expirations");
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);

        List<Warranty> expiringWarranties = warrantyRepository.findUpcomingExpirations(today, thirtyDaysFromNow);
        log.info("Found {} warranties expiring soon", expiringWarranties.size());

        for (Warranty warranty : expiringWarranties) {
            try {
                sendWarrantyExpiryAlert(warranty);
                warranty.setExpiryNotificationSent(true);
                warrantyRepository.save(warranty);
                log.info("Sent expiration notification for warranty ID: {}", warranty.getId());
            } catch (MessagingException e) {
                log.error("Failed to send warranty expiration alert for ID: {}", warranty.getId(), e);
            }
        }
    }

    private void checkUpcomingMaintenance() {
        log.info("Checking upcoming maintenance tasks");
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysFromNow = today.plusDays(7);

        List<MaintenanceSchedule> upcomingTasks = maintenanceRepository.findUpcomingMaintenance(today, sevenDaysFromNow);
        log.info("Found {} upcoming maintenance tasks", upcomingTasks.size());

        for (MaintenanceSchedule task : upcomingTasks) {
            try {
                sendMaintenanceReminderAlert(task);
                task.setNotificationSent(true);
                maintenanceRepository.save(task);
                log.info("Sent reminder for maintenance task ID: {}", task.getId());
            } catch (MessagingException e) {
                log.error("Failed to send maintenance reminder for ID: {}", task.getId(), e);
            }
        }
    }

    private void updateOverdueTasks() {
        log.info("Updating overdue maintenance tasks");
        LocalDate today = LocalDate.now();
        List<MaintenanceSchedule> overdueTasks = maintenanceRepository
                .findByStatusAndScheduledDateBefore("PENDING", today);

        for (MaintenanceSchedule task : overdueTasks) {
            task.setStatus("OVERDUE");
            maintenanceRepository.save(task);
            log.info("Updated task ID: {} to OVERDUE status", task.getId());
        }
    }

    public void sendWarrantyExpiryAlert(Warranty warranty) throws MessagingException {
        String subject = "Warranty Expiry Alert - " + warranty.getAsset().getName();
        String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;">
                    <h2 style="color: #dc3545;">Warranty Expiration Alert</h2>
                    <div style="background-color: #f8d7da; padding: 15px; margin: 15px 0; border-left: 4px solid #dc3545;">
                        <p><strong>Asset:</strong> %s</p>
                        <p><strong>Serial Number:</strong> %s</p>
                        <p><strong>Expiry Date:</strong> %s</p>
                        <p><strong>Days Until Expiry:</strong> %d</p>
                    </div>
                    <div style="margin-top: 20px;">
                        <h3>Vendor Details</h3>
                        <p><strong>Name:</strong> %s</p>
                        <p><strong>Email:</strong> %s</p>
                        <p><strong>Phone:</strong> %s</p>
                    </div>
                    <p style="margin-top: 20px;">Please take necessary action before warranty expires.</p>
                </div>
                """,
                warranty.getAsset().getName(),
                warranty.getAsset().getSerialNumber(),
                warranty.getExpiryDate(),
                LocalDate.now().until(warranty.getExpiryDate()).getDays(),
                warranty.getVendorName(),
                warranty.getVendorEmail(),
                warranty.getVendorPhone()
        );

        emailService.sendHtmlEmail("admin@dawms.com", subject, htmlContent);
    }

    private void sendMaintenanceReminderAlert(MaintenanceSchedule task) throws MessagingException {
        String subject = String.format("Maintenance Task Reminder: %s", task.getTaskDescription());
        String message = String.format("Dear %s,\n\nThis is a reminder for the maintenance task scheduled for %s.\n\n" +
            "Asset: %s\nDescription: %s\nPriority: %s\n\nPlease ensure this task is completed on schedule.",
            task.getAssignedTechnician() != null ? task.getAssignedTechnician().getName() : "Unassigned",
            task.getScheduledDate(),
            task.getAsset().getName(),
            task.getTaskDescription(),
            task.getPriority());

        emailService.sendHtmlEmail("admin@dawms.com", subject, message);
        // If technician email is available, send to them as well
        if (task.getAssignedTechnician() != null) {
            emailService.sendHtmlEmail(task.getAssignedTechnician().getEmail(), subject, message);
        }
    }

    public void sendNotification(Employee recipient, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        emailSender.send(mailMessage);
        log.info("Notification sent to: {}", recipient.getEmail());
    }
}