package com.mankind.api.product.dto.corporateaccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO for corporate account operations (create/update)")
public class CorporateAccountDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the corporate account", example = "ABC Corporation", required = true)
    private String name;

    @Schema(description = "Total purchases amount", example = "50000.00")
    private BigDecimal purchases;

    @Schema(description = "Number of orders placed", example = "25")
    private Integer numberOfOrdersPlaced;
}
