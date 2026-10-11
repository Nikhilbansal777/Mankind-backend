package com.mankind.api.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO for category operations (create/update)")
public class CategoryDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the category", example = "Electronics", required = true)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Description of the category", example = "Electronic devices and accessories")
    private String description;

    @Schema(description = "ID of the parent category (null for root categories)", example = "1")
    private Long parentId;
} 