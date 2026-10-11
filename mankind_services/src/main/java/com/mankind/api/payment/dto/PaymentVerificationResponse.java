package com.mankind.api.payment.dto;

import com.mankind.api.payment.model.PaymentProvider;
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
public class PaymentVerificationResponse {

    private Long id;
    private String stripePaymentIntentId;
    private String userId;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentProvider provider;
    private PaymentStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean paymentSucceeded;
}
