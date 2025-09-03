package com.mankind.api.product.dto.corporate;

import com.mankind.api.product.dto.product.ProductResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO for corporate price responses")
public class CorporatePriceResponseDTO {
    
    @Schema(description = "ID of the corporate price", example = "1")
    private Long id;
    
    @Schema(description = "ID of the corporate", example = "1")
    private Long corporateId;
    
    @Schema(description = "ID of the product", example = "1")
    private Long productId;
    
    @Schema(description = "Product details")
    private ProductResponseDTO product;
    
    @Schema(description = "Price in cents", example = "1999")
    private Long priceInCents;
    
    @Schema(description = "Currency code (ISO 4217)", example = "USD")
    private String currency;
    
    @Schema(description = "Date from which the price is effective", example = "2023-01-01T00:00:00")
    private LocalDateTime effectiveFrom;
    
    @Schema(description = "Date until which the price is effective (null means indefinite)", example = "2023-12-31T23:59:59")
    private LocalDateTime effectiveTo;
    
    @Schema(description = "User who last updated the price", example = "john.doe@example.com")
    private String updatedBy;
    
    @Schema(description = "Date when the price was last updated", example = "2023-01-01T12:34:56")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Date when the price was created", example = "2023-01-01T12:34:56")
    private LocalDateTime createdAt;
}