package com.mankind.matrix_order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO for applied coupon details")
public class OrderCouponDTO {
    @Schema(description = "Coupon code", example = "SAVE20")
    private String couponCode;

    @Schema(description = "Coupon name", example = "20% Off Sale")
    private String couponName;

    @Schema(description = "Discount amount", example = "20.00")
    private BigDecimal discountAmount;

    @Schema(description = "Discount type", example = "PERCENTAGE")
    private String discountType;

    @Schema(description = "Applied date")
    private LocalDateTime appliedAt;
}
