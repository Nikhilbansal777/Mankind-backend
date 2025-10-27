package com.mankind.api.product.dto.supplier;

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
@Schema(description = "Dashboard metrics for a supplier")
public class SupplierDashboardDTO {
    @Schema(description = "Supplier ID", example = "1")
    private Long supplierId;

    @Schema(description = "Supplier name", example = "Tech Supplies Inc.")
    private String supplierName;

    @Schema(description = "Total number of products linked to this supplier", example = "42")
    private long totalProducts;

    @Schema(description = "Number of active products", example = "38")
    private long activeProducts;

    @Schema(description = "Number of inactive products", example = "4")
    private long inactiveProducts;

    @Schema(description = "When the supplier record was created")
    private LocalDateTime createdAt;

    @Schema(description = "When the supplier record was last updated")
    private LocalDateTime updatedAt;
}
