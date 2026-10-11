package com.mankind.api.product.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO for supplier responses")
public class SupplierResponseDTO {
    @Schema(description = "Unique identifier of the supplier", example = "1")
    private Long id;

    @Schema(description = "Name of the supplier", example = "Tech Supplies Inc.")
    private String name;

    @Schema(description = "Name of the contact person", example = "John Smith")
    private String contactPerson;

    @Schema(description = "Phone number of the supplier", example = "+1-555-123-4567")
    private String phone;

    @Schema(description = "Email address of the supplier", example = "contact@techsupplies.com")
    private String email;

    @Schema(description = "Physical address of the supplier", example = "123 Tech Street, Suite 456")
    private String address;

    @Schema(description = "City of the supplier", example = "San Francisco")
    private String city;

    @Schema(description = "State/province of the supplier", example = "California")
    private String state;

    @Schema(description = "Zip/postal code of the supplier", example = "94105")
    private String zipCode;

    @Schema(description = "Country of the supplier", example = "United States")
    private String country;

    @Schema(description = "Additional notes about the supplier", example = "Preferred supplier for electronic components")
    private String notes;

    @Schema(description = "Whether the supplier is active", example = "true")
    private boolean isActive;

    @Schema(description = "List of product IDs supplied by this supplier")
    private List<Long> productIds;

    @Schema(description = "Timestamp when the supplier was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the supplier was last updated")
    private LocalDateTime updatedAt;
}