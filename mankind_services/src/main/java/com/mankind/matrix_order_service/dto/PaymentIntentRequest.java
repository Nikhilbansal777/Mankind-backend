package com.mankind.matrix_order_service.dto;

import com.mankind.api.payment.model.PaymentProvider;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentRequest {

    @NotNull(message = "Payment provider is required")
    private PaymentProvider provider;
}
