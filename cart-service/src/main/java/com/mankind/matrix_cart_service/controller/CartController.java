package com.mankind.matrix_cart_service.controller;

import com.mankind.matrix_cart_service.dto.CartItemDTO;
import com.mankind.matrix_cart_service.dto.CartResponseDTO;
import com.mankind.matrix_cart_service.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart API", description = "Endpoints for managing the authenticated user's shopping cart. All endpoints require authentication via JWT. Access through the gateway at /api/v1/cart.")
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    @Operation(summary = "Get current user's open cart", description = "Returns the authenticated user's open cart. Access via /api/v1/cart through the gateway.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current open cart returned successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required")
    })
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        CartResponseDTO cart = cartService.getCurrentUserOpenCart();
        if (cart == null) {
            throw new com.mankind.matrix_cart_service.exception.ResourceNotFoundException("Cart not found");
        }
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's open cart. If no open cart exists, one is created. Access via /api/v1/cart/items through the gateway.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item added to cart successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItem(@Valid @RequestBody CartItemDTO itemDTO) {
        log.info("Received addItem request: {}", itemDTO);
        return ResponseEntity.ok(cartService.addItemToCart(itemDTO));
    }

    @Operation(summary = "Update quantity of a cart item by product ID", description = "Updates the quantity of a product in the authenticated user's open cart by product ID. If quantity is set to zero, the item is removed. Access via /api/v1/cart/items/product/{productId} through the gateway.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart item quantity updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "404", description = "Product not found in cart")
    })
    @PatchMapping("/items/product/{productId}")
    public ResponseEntity<CartResponseDTO> updateQuantityByProduct(
            @Parameter(description = "ID of the product to update (not cart item ID)", required = true) @PathVariable Long productId,
            @Parameter(description = "New quantity", required = true) @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(productId, quantity));
    }

    @Operation(summary = "Remove item from cart", description = "Removes a product from the authenticated user's open cart. If the cart becomes empty, it is closed. Access via /api/v1/cart/items/product/{productId} through the gateway.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item removed from cart successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "404", description = "Product not found in cart")
    })
    @DeleteMapping("/items/product/{productId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @Parameter(description = "ID of the product to remove", required = true) @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(productId));
    }

    @Operation(summary = "Mark cart as converted", description = "Marks the current user's cart as CONVERTED when an order is created. Used by order service. Access via /api/v1/cart/convert through the gateway.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart marked as converted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @PostMapping("/convert")
    public ResponseEntity<CartResponseDTO> markCartAsConverted(
            @Parameter(description = "Order ID associated with the cart conversion", required = true) 
            @RequestParam("orderId") Long orderId) {
        return ResponseEntity.ok(cartService.markCartAsConverted(orderId));
    }
}
