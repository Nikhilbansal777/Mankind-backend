package com.mankind.matrix_cart_service.client;

import com.mankind.api.product.dto.product.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mankind.api.product.dto.inventory.InventoryResponseDTO;
import java.math.BigDecimal;

@FeignClient(name = "product-service", url = "${PRODUCT_SERVICE_URL:http://localhost:8080}")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id);

    @GetMapping("/inventory/{productId}")
    ResponseEntity<InventoryResponseDTO> getInventoryByProductId(@PathVariable Long productId);

    // Cart-related inventory operations
    @PostMapping("/inventory/{productId}/cart/reserve")
    ResponseEntity<InventoryResponseDTO> reserveStockForCart(
            @PathVariable Long productId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long userId,
            @RequestParam Long cartId);

    @PostMapping("/inventory/{productId}/cart/unreserve")
    ResponseEntity<InventoryResponseDTO> unreserveStockForCart(
            @PathVariable Long productId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long userId,
            @RequestParam Long cartId);

    @PutMapping("/inventory/{productId}/cart/update")
    ResponseEntity<InventoryResponseDTO> updateReservedStockForCart(
            @PathVariable Long productId,
            @RequestParam BigDecimal oldQuantity,
            @RequestParam BigDecimal newQuantity,
            @RequestParam Long userId,
            @RequestParam Long cartId);

    @PostMapping("/inventory/{productId}/cart/convert")
    ResponseEntity<InventoryResponseDTO> convertReservedToSold(
            @PathVariable Long productId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long orderId);
} 