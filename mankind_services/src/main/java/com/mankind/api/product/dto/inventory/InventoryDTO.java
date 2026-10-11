package com.mankind.api.product.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO for inventory operations (create/update)")
public class InventoryDTO {
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @Schema(description = "Price of the product", example = "999.99", required = true)
    private BigDecimal price;

    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    @Schema(description = "Currency code for the price", example = "USD", required = true)
    private String currency;

    @NotNull(message = "Available quantity is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Available quantity cannot be negative")
    @Schema(description = "Available quantity in stock", example = "10", required = true)
    private BigDecimal availableQuantity;

    @DecimalMin(value = "1.0", inclusive = true, message = "Max quantity per purchase must be at least 1")
    @Schema(description = "Maximum quantity allowed per purchase. If not set, no limit will be applied", example = "5", required = false)
    private BigDecimal maxQuantityPerPurchase;
} 