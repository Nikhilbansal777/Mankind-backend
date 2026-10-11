package com.mankind.matrix_coupon_service.dto;

import com.mankind.matrix_coupon_service.model.Coupon.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Request DTO for creating a new coupon")
public class CreateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 50, message = "Coupon code must be between 3 and 50 characters")
    @Schema(description = "Unique coupon code", example = "SAVE20")
    private String code;

    @NotBlank(message = "Coupon name is required")
    @Size(min = 1, max = 100, message = "Coupon name must be between 1 and 100 characters")
    @Schema(description = "Name of the coupon", example = "20% Off Sale")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Description of the coupon", example = "Get 20% off on all items")
    private String description;

    @NotNull(message = "Coupon type is required")
    @Schema(description = "Type of discount", example = "PERCENTAGE", allowableValues = {"PERCENTAGE", "FIXED_AMOUNT", "FREE_SHIPPING"})
    private CouponType type;

    @NotNull(message = "Coupon value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Value must be greater than 0")
    @Schema(description = "Discount value", example = "20.00")
    private BigDecimal value;

    @DecimalMin(value = "0.0", message = "Minimum order amount must be 0 or greater")
    @Schema(description = "Minimum order amount required to use coupon", example = "50.00")
    private BigDecimal minimumOrderAmount;

    @NotNull(message = "Maximum usage is required")
    @Min(value = 1, message = "Maximum usage must be at least 1")
    @Schema(description = "Maximum number of times this coupon can be used", example = "100")
    private Integer maxUsage;

    @NotNull(message = "Valid from date is required")
    @Schema(description = "Date and time when the coupon becomes valid", example = "2025-08-04T17:39:59.297Z")
    private LocalDateTime validFrom;

    @NotNull(message = "Valid to date is required")
    @Schema(description = "Date and time when the coupon expires", example = "2025-12-31T23:59:59.297Z")
    private LocalDateTime validTo;

    @NotNull(message = "One time use per user flag is required")
    @Schema(description = "Whether the coupon can only be used once per user", example = "true")
    private Boolean oneTimeUsePerUser;
} 