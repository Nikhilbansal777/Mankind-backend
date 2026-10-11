package com.mankind.matrix_product_service.controller;

import com.mankind.api.product.dto.inventory.InventoryDTO;
import com.mankind.api.product.dto.inventory.InventoryLogDTO;
import com.mankind.api.product.dto.inventory.InventoryResponseDTO;
import com.mankind.matrix_product_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/{productId}")
    @Operation(summary = "Create inventory", description = "Creates inventory information for a product with initial stock and price")
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.ok(inventoryService.createInventory(productId, inventoryDTO));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory", description = "Retrieves inventory information for a product")
    public ResponseEntity<InventoryResponseDTO> getInventory(
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update inventory", description = "Updates inventory stock and/or price information")
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.ok(inventoryService.updateInventory(productId, inventoryDTO));
    }

    @GetMapping("/{productId}/logs")
    @Operation(summary = "Get inventory logs", description = "Retrieves inventory movement logs for a product")
    public ResponseEntity<List<InventoryLogDTO>> getInventoryLogs(
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryLogs(productId));
    }

    // Cart-related inventory endpoints
    @PostMapping("/{productId}/cart/reserve")
    @Operation(summary = "Reserve stock for cart", description = "Reserves stock for cart operations")
    public ResponseEntity<InventoryResponseDTO> reserveStockForCart(
            @PathVariable Long productId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long userId,
            @RequestParam Long cartId) {
        return ResponseEntity.ok(inventoryService.reserveStockForCart(productId, quantity, userId, cartId));
    }

    @PostMapping("/{productId}/cart/unreserve")
    @Operation(summary = "Unreserve stock from cart", description = "Unreserves stock from cart operations")
    public ResponseEntity<InventoryResponseDTO> unreserveStockForCart(
            @PathVariable Long productId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long userId,
            @RequestParam Long cartId) {
        return ResponseEntity.ok(inventoryService.unreserveStockForCart(productId, quantity, userId, cartId));
    }

    @PutMapping("/{productId}/cart/update")
    @Operation(summary = "Update reserved stock for cart", description = "Updates reserved stock quantity for cart operations")
    public ResponseEntity<InventoryResponseDTO> updateReservedStockForCart(
            @PathVariable Long productId,
            @RequestParam BigDecimal oldQuantity,
            @RequestParam BigDecimal newQuantity,
            @RequestParam Long userId,
            @RequestParam Long cartId) {
        return ResponseEntity.ok(inventoryService.updateReservedStockForCart(productId, oldQuantity, newQuantity, userId, cartId));
    }

    @PostMapping("/{productId}/cart/convert")
    @Operation(summary = "Convert reserved stock to sold", description = "Moves reserved stock to sold when cart is converted to order")
    public ResponseEntity<InventoryResponseDTO> convertReservedToSold(
            @PathVariable Long productId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long orderId) {
        return ResponseEntity.ok(inventoryService.convertReservedToSold(productId, quantity, orderId));
    }
} 