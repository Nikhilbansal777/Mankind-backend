package com.mankind.api.product.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO for corporate pricing operations")
public class CorporatePricingDTO {
    @NotNull(message = "Corporate price is required")
    @Positive(message = "Corporate price must be greater than zero")
    @Schema(description = "Corporate price of the product", example = "899.99", required = true)
    private BigDecimal corporatePrice;

    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    @Schema(description = "Currency code for the corporate price", example = "USD", required = true)
    private String currency;
}
