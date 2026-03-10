package com.mankind.matrix_referral_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReferralCodeResponse {
    private Long userId;
    private String referralCode;
    private LocalDateTime createdAt;
}
