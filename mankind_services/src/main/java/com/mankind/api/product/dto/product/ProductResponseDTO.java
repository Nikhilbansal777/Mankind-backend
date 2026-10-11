package com.mankind.api.product.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.mankind.api.product.dto.inventory.InventoryStatusDTO;
import com.mankind.api.product.dto.category.CategoryResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "DTO for product responses")
public class ProductResponseDTO {
    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Schema(description = "Name of the product", example = "iPhone 13 Pro")
    private String name;

    @Schema(description = "Detailed description of the product", example = "Latest iPhone model with advanced camera system")
    private String description;

    @Schema(description = "Category information")
    private CategoryResponseDTO category;

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

    @Schema(description = "Whether the product is active", example = "true")
    private boolean active;

    @Schema(description = "Whether the product is featured", example = "false")
    private boolean isFeatured;

    @Schema(description = "Current inventory status")
    private InventoryStatusDTO inventoryStatus;

    @Schema(description = "Average rating of the product (null if no reviews)", example = "4.5")
    private Double averageRating;

    @Schema(description = "Timestamp when the product was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the product was last updated")
    private LocalDateTime updatedAt;

    @Schema(description = "Barcode of the product", example = "123456789012")
    private String barcode;
} 