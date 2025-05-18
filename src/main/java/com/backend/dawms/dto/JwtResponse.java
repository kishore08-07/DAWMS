package com.backend.dawms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    
    @Builder.Default
    private String type = "Bearer";
    
    private int id;
    private String email;
    private boolean verified;
    private List<String> roles;
} 