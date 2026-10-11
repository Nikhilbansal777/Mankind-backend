package com.mankind.api.payment.dto;

import com.mankind.api.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponse {

    private Long id;
    private String stripePaymentIntentId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String description;
    private String clientSecret;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
