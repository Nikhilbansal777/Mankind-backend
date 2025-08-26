package com.mankind.matrix_order_service.controller;

import com.mankind.matrix_order_service.dto.CreateOrderRequest;
import com.mankind.matrix_order_service.dto.OrderResponseDTO;
import com.mankind.matrix_order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order API", description = "Endpoints for managing user orders. All endpoints require authentication via JWT. Access through the gateway at /api/v1/orders.")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create order from cart", description = "Creates a new order from the current user's active cart. Cart status will be changed to CONVERTED. Shipping address must belong to the current user.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request - no active cart, invalid addresses, or coupon validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - addresses do not belong to current user")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Received create order request for current user");
        OrderResponseDTO order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Operation(summary = "Get user's order history", description = "Retrieves a paginated list of orders for the authenticated user. Supports pagination with page, size, and sort parameters.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required")
    })
    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getUserOrders(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrdersPaginated(pageable));
    }

    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID. Only accessible by the order owner.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - order does not belong to current user"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true) 
            @PathVariable Long orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Pay order", description = "Completes the payment for an order and updates cart status to CONVERTED. Only orders with PENDING status can be paid. Payment will change order status to CONFIRMED and payment status to PAID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order payment completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request - order cannot be paid (wrong status)"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - order does not belong to current user"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponseDTO> payOrder(
            @Parameter(description = "ID of the order to pay", required = true) 
            @PathVariable Long orderId) {
        OrderResponseDTO order = orderService.payOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Cancel order", description = "Cancels an order. Only orders with PENDING status can be cancelled. Order status will be changed to CANCELLED.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request - order cannot be cancelled (wrong status)"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - order does not belong to current user"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true) 
            @PathVariable Long orderId) {
        OrderResponseDTO order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }

}
