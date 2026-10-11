package com.mankind.matrix_order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for order item details")
public class OrderItemDTO {
    @Schema(description = "Order item ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product name", example = "iPhone 13 Pro")
    private String productName;

    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Quantity", example = "2")
    private Integer quantity;

    @Schema(description = "Subtotal", example = "1999.98")
    private BigDecimal subtotal;
}
