package com.mankind.matrix_payment_service.dto;

import com.mankind.matrix_payment_service.model.PaymentProvider;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (e.g., USD, EUR)")
    private String currency;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Map<String, String> metadata;

    @NotNull(message = "Payment provider is required")
    private PaymentProvider provider;

    @Builder.Default
    private String paymentMethodTypes = "card";

    @Builder.Default
    private Boolean automaticPaymentMethods = true;
}
