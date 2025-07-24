package com.mankind.matrix_cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private String sessionId;
    private String status;
    private List<CartItemResponseDTO> items;
    private double subtotal;
    private double total;
} 