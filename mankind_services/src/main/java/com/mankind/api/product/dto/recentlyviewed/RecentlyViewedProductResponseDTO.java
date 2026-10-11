package com.mankind.api.product.dto.recentlyviewed;

import com.mankind.api.product.dto.product.ProductResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for recently viewed product")
public class RecentlyViewedProductResponseDTO {
    
    @Schema(description = "ID of the recently viewed product entry", example = "1")
    private Long id;
    
    @Schema(description = "ID of the user who viewed the product", example = "42")
    private Long userId;
    
    @Schema(description = "Product details")
    private ProductResponseDTO product;
    
    @Schema(description = "Date and time when the product was first viewed", example = "2023-06-15T14:30:00")
    private LocalDateTime viewedAt;
    
    @Schema(description = "Date and time when the product was last viewed", example = "2023-06-16T09:45:00")
    private LocalDateTime lastViewedAt;
}