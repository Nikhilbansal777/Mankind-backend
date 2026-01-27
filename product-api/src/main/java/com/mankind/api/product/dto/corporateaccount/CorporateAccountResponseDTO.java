package com.mankind.api.product.dto.corporateaccount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO for corporate account responses")
public class CorporateAccountResponseDTO {
    @Schema(description = "Unique identifier of the corporate account", example = "1")
    private Long id;

    @Schema(description = "Name of the corporate account", example = "ABC Corporation")
    private String name;

    @Schema(description = "Total purchases amount", example = "50000.00")
    private BigDecimal purchases;

    @Schema(description = "Number of orders placed", example = "25")
    private Integer numberOfOrdersPlaced;

    @Schema(description = "Date when the corporate account joined our system", example = "2023-06-15T10:30:00")
    private LocalDateTime dateOfJoined;

    @Schema(description = "Whether the corporate account is active", example = "true")
    private boolean isActive;

    @Schema(description = "Timestamp when the account was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the account was last updated")
    private LocalDateTime updatedAt;
}
