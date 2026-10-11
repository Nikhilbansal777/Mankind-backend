package com.mankind.api.product.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO for inventory responses")
public class InventoryResponseDTO {
    @Schema(description = "Unique identifier of the inventory", example = "1")
    private Long id;

    @Schema(description = "ID of the associated product", example = "1")
    private Long productId;

    @Schema(description = "Name of the associated product", example = "iPhone 13 Pro")
    private String productName;

    @Schema(description = "Price of the product", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Formatted price display", example = "$999.99")
    private String priceDisplay;

    @Schema(description = "Currency code for the price", example = "USD")
    private String currency;

    @Schema(description = "Available quantity in stock", example = "10")
    private BigDecimal availableQuantity;

    @Schema(description = "Reserved quantity", example = "2")
    private BigDecimal reservedQuantity;

    @Schema(description = "Sold quantity", example = "50")
    private BigDecimal soldQuantity;

    @Schema(description = "Timestamp when the inventory was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the inventory was last updated")
    private LocalDateTime updatedAt;

    @Schema(description = "Maximum quantity allowed per purchase. If not set, no limit will be applied", example = "5")
    private BigDecimal maxQuantityPerPurchase;

    @Schema(description = "Current inventory status", example = "IN_STOCK", allowableValues = {"NO_INVENTORY", "OUT_OF_STOCK", "IN_STOCK"})
    private String status;
} 