package com.mankind.api.payment.dto;

import com.mankind.api.payment.model.PaymentProvider;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentIntentRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @Builder.Default
    private String currency = "USD";

    @Builder.Default
    private String description = "";

    private Map<String, String> metadata;

    @NotNull(message = "Payment provider is required")
    private PaymentProvider provider;

    @Builder.Default
    private String paymentMethodTypes = "card";

    @Builder.Default
    private Boolean automaticPaymentMethods = true;
}
