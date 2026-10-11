package com.mankind.matrix_order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Request DTO for creating a new order")
public class CreateOrderRequest {
    @NotNull(message = "Shipping address ID is required")
    @Schema(description = "Shipping address ID", example = "1")
    private Long shippingAddressId;

    @NotNull(message = "Shipping value is required")
    @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = true, message = "Shipping value must be greater than or equal to 0")
    @Schema(description = "Shipping cost", example = "15.99")
    private BigDecimal shippingValue;

    @Schema(description = "Coupon code to apply", example = "SAVE20")
    private String couponCode;

    @Schema(description = "Additional notes for the order")
    private String notes;

    @Schema(description = "Delivery type", example = "STANDARD", allowableValues = {"STANDARD", "EXPRESS"})
    private String deliveryType;

    @Schema(description = "Shipping date for delivery", example = "2024-12-25")
    private LocalDate shippingDate;
}
