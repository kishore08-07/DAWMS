package com.backend.dawms.service;

import com.backend.dawms.dto.AuthRequest;
import com.backend.dawms.dto.AuthResponse;
import com.backend.dawms.dto.OtpRequest;
import com.backend.dawms.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    AuthResponse verifyOtp(OtpRequest request);
}
