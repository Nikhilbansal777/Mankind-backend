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
public class PaymentVerificationRequest {

    private String orderId;
    private String paymentIntentId;
}
