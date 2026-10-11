package com.mankind.matrix_order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for order details")
public class OrderResponseDTO {
    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Unique order number", example = "ORD-20241201-143022-12345")
    private String orderNumber;

    @Schema(description = "User ID", example = "123")
    private Long userId;

    @Schema(description = "Cart ID", example = "1")
    private Long cartId;

    @Schema(description = "Order status", example = "PENDING")
    private String status;

    @Schema(description = "Payment status", example = "PENDING")
    private String paymentStatus;

    @Schema(description = "Subtotal (sum of all items)", example = "100.00")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "8.50")
    private BigDecimal tax;

    @Schema(description = "Total discounts from coupons", example = "20.00")
    private BigDecimal discounts;

    @Schema(description = "Total amount after all calculations", example = "88.50")
    private BigDecimal total;

    @Schema(description = "Shipping cost", example = "15.99")
    private BigDecimal shippingValue;

    @Schema(description = "Payment ID", example = "pay_123456789")
    private String paymentId;

    @Schema(description = "Shipping address ID", example = "1")
    private Long shippingAddressId;

    @Schema(description = "Delivery type", example = "STANDARD", allowableValues = {"STANDARD", "EXPRESS"})
    private String deliveryType;

    @Schema(description = "Shipping date for delivery", example = "2024-12-25")
    private LocalDate shippingDate;

    @Schema(description = "Order notes")
    private String notes;

    @Schema(description = "Order items")
    private List<OrderItemDTO> items;

    @Schema(description = "Applied coupon")
    private OrderCouponDTO appliedCoupon;

    @Schema(description = "Coupon code applied to the order", example = "SAVE20")
    private String couponCode;

    @Schema(description = "Type of discount applied", example = "PERCENTAGE", allowableValues = {"PERCENTAGE", "FIXED_AMOUNT", "FREE_SHIPPING"})
    private String discountType;

    @Schema(description = "Order creation date")
    private LocalDateTime createdAt;

    @Schema(description = "Order last update date")
    private LocalDateTime updatedAt;
}
