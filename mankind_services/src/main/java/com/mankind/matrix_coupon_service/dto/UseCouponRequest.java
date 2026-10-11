package com.mankind.matrix_coupon_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "Request DTO for using a coupon")
public class UseCouponRequest {

    @NotNull(message = "Coupon code is required")
    @Schema(description = "Coupon code to use", example = "SAVE20")
    private String code;

    @Schema(description = "Order ID where the coupon is being used (optional)", example = "456")
    private Long orderId;
} 