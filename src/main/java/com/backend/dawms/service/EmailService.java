package com.backend.dawms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        
        String htmlMsg = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;">
                    <h2 style="color: #4a4a4a;">Verify Your Email</h2>
                    <p>Thank you for registering with DAWMS. To complete your registration, please use the following OTP:</p>
                    <div style="background-color: #f2f2f2; padding: 10px; text-align: center; font-size: 24px; letter-spacing: 5px; margin: 20px 0;">
                        <strong>%s</strong>
                    </div>
                    <p>This OTP is valid for 10 minutes. If you didn't request this, please ignore this email.</p>
                    <p>Best regards,<br/>DAWMS Team</p>
                </div>
                """.formatted(otp);
        
        helper.setText(htmlMsg, true);
        helper.setTo(to);
        helper.setSubject("DAWMS - Email Verification OTP");
        helper.setFrom("noreply@dawms.com");
        
        mailSender.send(mimeMessage);
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        
        helper.setText(htmlContent, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("noreply@dawms.com");
        
        mailSender.send(mimeMessage);
    }
} 