package com.mankind.api.product.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO for corporate pricing response")
public class CorporatePricingResponseDTO {
    @Schema(description = "ID of the associated product", example = "1")
    private Long productId;

    @Schema(description = "Name of the associated product", example = "iPhone 13 Pro")
    private String productName;

    @Schema(description = "Regular price of the product", example = "999.99")
    private BigDecimal regularPrice;

    @Schema(description = "Formatted regular price display", example = "USD 999.99")
    private String regularPriceDisplay;

    @Schema(description = "Corporate price of the product", example = "899.99")
    private BigDecimal corporatePrice;

    @Schema(description = "Formatted corporate price display", example = "USD 899.99")
    private String corporatePriceDisplay;

    @Schema(description = "Currency code for the prices", example = "USD")
    private String currency;

    @Schema(description = "Username of the corporate user who last updated the price", example = "corporate_user")
    private String updatedByCorporate;

    @Schema(description = "Timestamp when the corporate price was last updated")
    private LocalDateTime lastUpdatedAtCorporate;
}
