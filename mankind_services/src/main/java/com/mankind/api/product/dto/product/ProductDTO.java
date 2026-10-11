package com.mankind.api.product.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "DTO for product operations (create/update)")
public class ProductDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    @Schema(description = "Name of the product", example = "iPhone 13 Pro", required = true)
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Schema(description = "Detailed description of the product", example = "Latest iPhone model with advanced camera system")
    private String description;

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the category this product belongs to", example = "1", required = true)
    private Long categoryId;

    @Schema(description = "SKU (Stock Keeping Unit) of the product", example = "IP13P-256-BLK")
    private String sku;

    @Schema(description = "Brand of the product", example = "Apple")
    private String brand;

    @Schema(description = "Model of the product", example = "A2482")
    private String model;

    @Schema(description = "Map of product specifications", example = "{\"color\": \"Black\", \"storage\": \"256GB\", \"ram\": \"6GB\"}")
    private Map<String, String> specifications;

    @Schema(description = "List of image URLs for the product")
    private List<String> images;

    @Schema(description = "Whether the product is featured", example = "false")
    private Boolean isFeatured;
} 