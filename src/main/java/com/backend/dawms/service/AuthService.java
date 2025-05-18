package com.backend.dawms.service;

import com.backend.dawms.dto.JwtResponse;
import com.backend.dawms.dto.LoginRequest;
import com.backend.dawms.dto.SignupRequest;
import com.backend.dawms.enums.RoleName;
import com.backend.dawms.model.OtpVerification;
import com.backend.dawms.model.Role;
import com.backend.dawms.model.User;
import com.backend.dawms.repository.OtpVerificationRepository;
import com.backend.dawms.repository.RoleRepository;
import com.backend.dawms.repository.UserRepository;
import com.backend.dawms.security.JwtTokenProvider;
import com.backend.dawms.security.UserDetailsImpl;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    
    @Value("${app.otp.expirationMinutes}")
    private int otpExpirationMinutes;
    
    @Transactional
    public String register(SignupRequest signupRequest) throws MessagingException {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        
        // Create new user with unverified status
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .verified(false)
                .build();
        
        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            // Default role is EMPLOYEE if not specified
            Role userRole = roleRepository.findByName(RoleName.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(userRole);
        } else {
            signupRequest.getRoles().forEach(roleName -> {
                try {
                    Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role: " + roleName);
                }
            });
        }
        
        user.setRoles(roles);
        userRepository.save(user);
        
        // Generate and send OTP
        String otp = generateOtp();
        saveOtp(signupRequest.getEmail(), otp);
        emailService.sendOtpEmail(signupRequest.getEmail(), otp);
        
        return "User registered successfully. Please verify your email with the OTP sent.";
    }
    
    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        //Authenticates the user using Spring Security.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        //Stores the authentication info in the security context.
        //Spring Security’s way of storing who is logged in
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Gets the authenticated user's details.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //Ensures the account is verified before logging in.
        if (!userDetails.isEnabled()) {
            throw new RuntimeException("Account is not verified. Please verify your email first.");
        }
        
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        
        return JwtResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .verified(userDetails.isEnabled())
                .roles(roles)
                .build();
    }
    
    @Transactional
    public String verifyOtp(String email, String otpCode) {
        OtpVerification otpVerification = otpVerificationRepository.findByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        
        if (otpVerification.isExpired()) {
            throw new RuntimeException("OTP has expired");
        }
        
        if (otpVerification.isVerified()) {
            throw new RuntimeException("OTP already used");
        }
        
        otpVerification.setVerifiedAt(LocalDateTime.now());
        otpVerificationRepository.save(otpVerification);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setVerified(true);
        userRepository.save(user);
        
        return "Email verified successfully";
    }
    
    @Transactional
    public String resendOtp(String email) throws MessagingException {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email not registered");
        }
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isVerified()) {
            throw new RuntimeException("Email already verified");
        }
        
        // Check if there's a recent OTP that hasn't expired
        Optional<OtpVerification> latestOtp = otpVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (latestOtp.isPresent() && !latestOtp.get().isExpired() && !latestOtp.get().isVerified()) {
            // If there's a valid OTP, resend the same one
            emailService.sendOtpEmail(email, latestOtp.get().getOtpCode());
            return "OTP resent successfully";
        }
        
        // Otherwise, generate a new OTP
        String otp = generateOtp();
        saveOtp(email, otp);
        emailService.sendOtpEmail(email, otp);
        
        return "New OTP sent successfully";
    }
    
    private String generateOtp() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    private void saveOtp(String email, String otp) {
        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otpCode(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .build();
        
        otpVerificationRepository.save(otpVerification);
    }
}
