package com.mankind.api.product.dto.corporate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO for corporate price operations (create/update)")
public class CorporatePriceDTO {
    
    @NotNull(message = "Product ID is required")
    @Schema(description = "ID of the product", example = "1")
    private Long productId;
    
    @NotNull(message = "Price in cents is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Schema(description = "Price in cents", example = "1999")
    private Long priceInCents;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    @Schema(description = "Currency code (ISO 4217)", example = "USD")
    private String currency;
    
    @NotNull(message = "Effective from date is required")
    @Schema(description = "Date from which the price is effective", example = "2023-01-01T00:00:00")
    private LocalDateTime effectiveFrom;
    
    @Schema(description = "Date until which the price is effective (null means indefinite)", example = "2023-12-31T23:59:59")
    private LocalDateTime effectiveTo;
}