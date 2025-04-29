package com.backend.dawms.repository;

import com.backend.dawms.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmailAndOtpCode(String email, String otpCode);
    
    Optional<OtpVerification> findTopByEmailOrderByCreatedAtDesc(String email);
} 