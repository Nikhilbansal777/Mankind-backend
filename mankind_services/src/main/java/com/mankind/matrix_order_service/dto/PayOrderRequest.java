package com.mankind.matrix_order_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderRequest {

    @NotBlank(message = "Payment Intent ID is required")
    private String paymentIntentId;
}
