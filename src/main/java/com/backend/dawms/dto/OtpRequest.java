package com.backend.dawms.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpRequest {
    private String email;
    private String otp;
}
