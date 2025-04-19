package com.backend.dawms.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {
    private final Map<String, String> otpMap = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void generateOtp(String email) {
        String otp = String.valueOf(100000 + random.nextInt(900000));
        otpMap.put(email, otp);
        System.out.println("OTP for " + email + ": " + otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        return otpMap.containsKey(email) && otpMap.get(email).equals(otp);
    }
}
