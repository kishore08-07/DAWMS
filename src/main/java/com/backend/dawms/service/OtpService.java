package com.backend.dawms.service;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {
    void generateOtp(String email);
    boolean verifyOtp(String email, String otp);
}
