package com.mankind.api.product.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLogDTO {
    private Long id;
    private Long inventoryId;
    private String action;
    private Integer quantity;
    private String description;
    private LocalDateTime createdAt;
    private String createdBy;
    private Long userId;
    private Long cartId;
} 