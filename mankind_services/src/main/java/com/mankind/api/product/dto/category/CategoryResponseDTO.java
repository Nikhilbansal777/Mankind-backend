package com.mankind.api.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO for category responses")
public class CategoryResponseDTO {
    @Schema(description = "Unique identifier of the category", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Schema(description = "Description of the category", example = "Electronic devices and accessories")
    private String description;

    @Schema(description = "ID of the parent category (null for root categories)", example = "1")
    private Long parentId;

    @Schema(description = "List of subcategories")
    private List<CategoryResponseDTO> subcategories;

    @Schema(description = "Timestamp when the category was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the category was last updated")
    private LocalDateTime updatedAt;
} 