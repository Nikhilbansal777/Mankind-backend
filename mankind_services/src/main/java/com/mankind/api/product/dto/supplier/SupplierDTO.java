package com.mankind.api.product.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO for supplier operations (create/update)")
public class SupplierDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the supplier", example = "Tech Supplies Inc.", required = true)
    private String name;

    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    @Schema(description = "Name of the contact person", example = "John Smith")
    private String contactPerson;

    @Size(max = 50, message = "Phone number cannot exceed 50 characters")
    @Schema(description = "Phone number of the supplier", example = "+1-555-123-4567")
    private String phone;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Schema(description = "Email address of the supplier", example = "contact@techsupplies.com")
    private String email;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Schema(description = "Physical address of the supplier", example = "123 Tech Street, Suite 456")
    private String address;

    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Schema(description = "City of the supplier", example = "San Francisco")
    private String city;

    @Size(max = 50, message = "State cannot exceed 50 characters")
    @Schema(description = "State/province of the supplier", example = "California")
    private String state;

    @Size(max = 20, message = "Zip code cannot exceed 20 characters")
    @Schema(description = "Zip/postal code of the supplier", example = "94105")
    private String zipCode;

    @Size(max = 50, message = "Country cannot exceed 50 characters")
    @Schema(description = "Country of the supplier", example = "United States")
    private String country;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Schema(description = "Additional notes about the supplier", example = "Preferred supplier for electronic components")
    private String notes;

    @Schema(description = "Whether the supplier is active", example = "true")
    private Boolean isActive;
}