package com.backend.dawms.service;

import com.backend.dawms.config.JwtService;
import com.backend.dawms.dto.AuthRequest;
import com.backend.dawms.dto.AuthResponse;
import com.backend.dawms.dto.OtpRequest;
import com.backend.dawms.dto.RegisterRequest;
import com.backend.dawms.enums.RoleName;
import com.backend.dawms.model.Role;
import com.backend.dawms.model.User;
import com.backend.dawms.repository.RoleRepository;
import com.backend.dawms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            return AuthResponse.builder().message("User already exists").build();
        }
        Role role = (Role) roleRepo.findByName(RoleName.valueOf(request.getRole()))
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(role))
                .verified(false)
                .build();

        userRepo.save(user);
        otpService.generateOtp(request.getEmail());
        return AuthResponse.builder().message("OTP sent to email").build();
    }

    @Override
    public AuthResponse verifyOtp(OtpRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            user.setVerified(true);
            userRepo.save(user);
            String jwt = jwtService.generateToken(user);
            return AuthResponse.builder().token(jwt).message("Verified successfully").build();
        }
        return AuthResponse.builder().message("Invalid OTP").build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isVerified()) return AuthResponse.builder().message("Email not verified").build();

        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
        ));
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).message("Login successful").build();
    }
}
