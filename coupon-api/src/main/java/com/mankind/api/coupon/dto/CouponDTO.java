package com.mankind.api.coupon.dto;

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
public class CouponDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private CouponType type;
    private BigDecimal value;
    private BigDecimal minimumOrderAmount;
    private Integer maxUsage;
    private Integer currentUsage;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean isActive;
    private Boolean oneTimeUsePerUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CouponType {
        PERCENTAGE,
        FIXED_AMOUNT,
        FREE_SHIPPING
    }
}
