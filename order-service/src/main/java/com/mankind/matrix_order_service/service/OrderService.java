package com.mankind.matrix_order_service.service;

import com.mankind.api.cart.dto.CartResponseDTO;
import com.mankind.api.cart.dto.CartItemResponseDTO;
import com.mankind.api.coupon.dto.CouponDTO;
import com.mankind.api.user.dto.AddressDTO;
import com.mankind.matrix_order_service.client.CartClient;
import com.mankind.matrix_order_service.client.CouponClient;
import com.mankind.matrix_order_service.client.UserClient;
import com.mankind.matrix_order_service.dto.CreateOrderRequest;
import com.mankind.matrix_order_service.dto.OrderResponseDTO;
import com.mankind.matrix_order_service.dto.OrderItemDTO;
import com.mankind.matrix_order_service.dto.OrderCouponDTO;
import com.mankind.matrix_order_service.exception.CartValidationException;
import com.mankind.matrix_order_service.exception.CouponValidationException;
import com.mankind.matrix_order_service.exception.OrderCreationException;
import com.mankind.matrix_order_service.exception.OrderNotFoundException;
import com.mankind.matrix_order_service.exception.AccessDeniedException;
import com.mankind.matrix_order_service.model.Order;
import com.mankind.matrix_order_service.model.OrderItem;
import com.mankind.matrix_order_service.model.OrderStatusHistory;
import com.mankind.matrix_order_service.repository.OrderRepository;
import com.mankind.matrix_order_service.repository.OrderItemRepository;
import com.mankind.matrix_order_service.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final CartClient cartClient;
    private final CouponClient couponClient;
    private final UserClient userClient;
    private final CurrentUserService currentUserService;
    private final OrderNumberGenerator orderNumberGenerator;
    

    /**
     * Creates a new order from the current user's active cart or updates an existing order if cart items changed.
     * 
     * @param request The order creation request containing shipping address, shipping value, coupon code, and notes
     * @return OrderResponseDTO containing the created or updated order details
     * @throws CartValidationException if cart validation fails, addresses are invalid, or shipping value is invalid
     * @throws OrderCreationException if order creation or update fails
     */
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequest request) {
        log.info("Creating order for current user");

        // Get current user
        Long userId = currentUserService.getCurrentUserId();
        log.info("Current user ID: {}", userId);

        // Validate cart and addresses
        CartResponseDTO cart = validateAndGetCart(userId);
        log.info("Cart validated: ID={}, Items={}, Status={}", cart.getId(), cart.getItems().size(), cart.getStatus());
        
        validateAddresses(request, userId);
        log.info("Addresses validated: shippingAddressId={}", request.getShippingAddressId());

        // Validate shipping value
        validateShippingValue(request);
        log.info("Shipping value validated: {}", request.getShippingValue());

        // Check if order already exists for this cart
        Order existingOrder = findOrderByCartId(cart.getId());
        
        if (existingOrder != null) {
            log.info("Order already exists for cart {}, updating with new request data. Order ID: {}", cart.getId(), existingOrder.getId());
            return handleExistingOrder(existingOrder, cart, request);
        } else {
            log.info("Creating new order for cart {}", cart.getId());
            return createNewOrder(request, userId, cart);
        }
    }

    private OrderResponseDTO handleExistingOrder(Order existingOrder, CartResponseDTO cart, CreateOrderRequest request) {
        // Always update the order with new request data, regardless of cart changes
        log.info("Updating existing order {} with new request data", existingOrder.getId());
        return updateExistingOrder(existingOrder, cart, request);
    }

    private OrderResponseDTO createNewOrder(CreateOrderRequest request, Long userId, CartResponseDTO cart) {
        // Generate order number and calculate totals
        String orderNumber = orderNumberGenerator.generateOrderNumber();

        // Round shipping value to 2 decimal places
        BigDecimal roundedShippingValue = request.getShippingValue().setScale(2, RoundingMode.HALF_UP);
        OrderCalculation calculation = calculateOrderTotals(cart, request.getCouponCode(), roundedShippingValue);

        // Create and save order
        Order order = createOrderEntity(request, userId, cart, orderNumber, calculation, roundedShippingValue);
        final Order savedOrder = orderRepository.save(order);

        // Process order items
        List<OrderItem> orderItems = createOrderItems(cart, savedOrder);
        
        // Save order items to database
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        // Create status history (cart status update moved to payment endpoint)
        createOrderStatusHistory(savedOrder, "Order created from cart");

        log.info("Order created successfully: {} with {} items", orderNumber, savedOrderItems.size());

        return buildOrderResponseDTO(savedOrder, savedOrderItems, calculation);
    }

    @Transactional
    private OrderResponseDTO updateExistingOrder(Order existingOrder, CartResponseDTO cart, CreateOrderRequest request) {
        // Round shipping value to 2 decimal places
        BigDecimal roundedShippingValue = request.getShippingValue().setScale(2, RoundingMode.HALF_UP);
        OrderCalculation calculation = calculateOrderTotals(cart, request.getCouponCode(), roundedShippingValue);

        log.info("Updating order {} with calculation results - Subtotal: {}, Discounts: {}, Tax: {}, Total: {}", 
                existingOrder.getId(), calculation.getSubtotal(), calculation.getDiscounts(), 
                calculation.getTax(), calculation.getTotal());

        // Update order with new request data
        existingOrder.setSubtotal(calculation.getSubtotal());
        existingOrder.setTax(calculation.getTax());
        existingOrder.setDiscounts(calculation.getDiscounts());
        existingOrder.setTotal(calculation.getTotal());
        existingOrder.setShippingValue(roundedShippingValue);
        
        // Handle coupon code - can be null to remove coupon
        existingOrder.setCouponCode(request.getCouponCode());
        existingOrder.setDiscountType(calculation.getAppliedCoupon() != null ? calculation.getAppliedCoupon().getType().name() : null);
        
        existingOrder.setNotes(request.getNotes());
        existingOrder.setShippingAddressId(request.getShippingAddressId());
        existingOrder.setDeliveryType(request.getDeliveryType() != null ? 
            Order.DeliveryType.valueOf(request.getDeliveryType().toUpperCase()) : Order.DeliveryType.STANDARD);
        existingOrder.setShippingDate(request.getShippingDate());
        existingOrder.setUpdatedAt(LocalDateTime.now());

        log.info("Order {} updated with - CouponCode: {}, DiscountType: {}, Discounts: {}", 
                existingOrder.getId(), existingOrder.getCouponCode(), 
                existingOrder.getDiscountType(), existingOrder.getDiscounts());

        // Save updated order
        Order updatedOrder = orderRepository.save(existingOrder);

        // Always update order items to ensure they match current cart
        List<OrderItem> updatedOrderItems = updateOrderItems(updatedOrder, cart);

        // Create status history for update
        String statusNote = request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty() 
            ? "Order updated with coupon: " + request.getCouponCode() 
            : "Order updated (coupon removed)";
        createOrderStatusHistory(updatedOrder, statusNote);

        // Handle coupon changes
        handleCouponChanges(existingOrder, cart, request);

        log.info("Order {} updated successfully with new data and {} items", existingOrder.getOrderNumber(), updatedOrderItems.size());

        return buildOrderResponseDTO(updatedOrder, updatedOrderItems, calculation);
    }

    @Transactional
    private List<OrderItem> updateOrderItems(Order order, CartResponseDTO cart) {
        try {
            log.info("Starting order items update for order {} with cart {} items", order.getId(), cart.getItems().size());
            
            // Remove existing order items
            List<OrderItem> existingItems = orderItemRepository.findByOrderId(order.getId());
            log.info("Found {} existing order items for order {}", existingItems.size(), order.getId());
            
            if (!existingItems.isEmpty()) {
                orderItemRepository.deleteByOrderId(order.getId());
                log.info("Deleted {} existing order items for order {}", existingItems.size(), order.getId());
            }
            
            // Create new order items from current cart
            List<OrderItem> newOrderItems = createOrderItems(cart, order);
            log.info("Created {} new order items for order {}", newOrderItems.size(), order.getId());
            
            // Save all new order items
            List<OrderItem> savedOrderItems = orderItemRepository.saveAll(newOrderItems);
            log.info("Successfully saved {} order items to database for order {}", savedOrderItems.size(), order.getId());
            
            // Verify the items were saved by querying them back
            List<OrderItem> verifiedItems = orderItemRepository.findByOrderId(order.getId());
            log.info("Verified {} order items in database for order {}", verifiedItems.size(), order.getId());
            
            return savedOrderItems;
        } catch (Exception e) {
            log.error("Failed to update order items for order {}: {}", order.getId(), e.getMessage(), e);
            throw new OrderCreationException("Failed to update order items: " + e.getMessage());
        }
    }

    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = findOrderById(orderId);
        validateOrderOwnership(order);
        return buildOrderResponseDTO(order, null, null);
    }

    public Page<OrderResponseDTO> getUserOrdersPaginated(Pageable pageable) {
        Long userId = currentUserService.getCurrentUserId();
        Page<Order> ordersPage = orderRepository.findByUserIdOrderByCreatedAtDesc(userId.toString(), pageable);
        return ordersPage.map(order -> buildOrderResponseDTO(order, null, null));
    }

    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);
        validateOrderOwnership(order);
        validateOrderCanBeCancelled(order);

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        createOrderStatusHistory(order, "Order cancelled by user");
        return buildOrderResponseDTO(order, null, null);
    }

    /**
     * Completes the payment for an order, changing its status to CONFIRMED and payment status to PAID.
     * Also updates the associated cart status to CONVERTED.
     * 
     * @param orderId The ID of the order to pay
     * @return OrderResponseDTO containing the updated order details
     * @throws OrderNotFoundException if the order is not found
     * @throws AccessDeniedException if the user doesn't own the order
     * @throws CartValidationException if the order cannot be paid (wrong status or already paid)
     * @throws OrderCreationException if cart status update fails
     */
    @Transactional
    public OrderResponseDTO payOrder(Long orderId) {
        log.info("Starting payment process for order: {}", orderId);
        
        try {
            // Step 1: Validate order and ownership
            Order order = findOrderById(orderId);
            validateOrderOwnership(order);
            validateOrderCanBePaid(order);
            
            log.info("Order {} validation passed - Status: {}, PaymentStatus: {}", 
                    orderId, order.getStatus(), order.getPaymentStatus());
            
            // Step 2: Simulate successful payment (to be replaced by external service in the future)
            log.info("Simulating payment success for order {}", order.getOrderNumber());
            
            // Step 3: Update order status to CONFIRMED and payment status to PAID
            order.setStatus(Order.OrderStatus.CONFIRMED);
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setUpdatedAt(LocalDateTime.now());
            order = orderRepository.save(order);
            
            log.info("Order {} status updated - Status: {}, PaymentStatus: {}", 
                    orderId, order.getStatus(), order.getPaymentStatus());

            // Step 4: Update cart status to CONVERTED via external service
            updateCartStatus(order.getCartId(), orderId);

            // Step 5: Mark coupon as used if applied
            markCouponAsUsedIfApplied(order);

            // Step 6: Create comprehensive status history
            createOrderStatusHistory(order, "Order payment completed successfully - Status: CONFIRMED, Payment: PAID, Cart: CONVERTED");

            log.info("Order {} payment completed successfully - Order: CONFIRMED, Payment: PAID, Cart: CONVERTED", 
                    order.getOrderNumber());
            
            return buildOrderResponseDTO(order, null, null);
            
        } catch (Exception e) {
            log.error("Payment failed for order {}: {}", orderId, e.getMessage(), e);
            
            // Create failure history if order exists
            try {
                Order order = findOrderById(orderId);
                createOrderStatusHistory(order, "Payment failed: " + e.getMessage());
            } catch (Exception historyException) {
                log.warn("Could not create payment failure history for order {}: {}", orderId, historyException.getMessage());
            }
            
            throw e;
        }
    }


    // Private helper methods

    // ============================================================================
    // VALIDATION HELPER METHODS
    // ============================================================================

    private CartResponseDTO validateAndGetCart(Long userId) {
        try {
            CartResponseDTO cart = cartClient.getCurrentUserCart();
            if (cart == null) {
                throw new CartValidationException("No active cart found for current user. Please add items to your cart before creating an order.");
            }

            if (!"ACTIVE".equals(cart.getStatus())) {
                throw new CartValidationException("Cart is not active. Current status: " + cart.getStatus());
            }

            return cart;
        } catch (Exception e) {
            log.error("Failed to get current user cart: {}", e.getMessage());
            throw createCartValidationException(e);
        }
    }

    private void validateAddresses(CreateOrderRequest request, Long userId) {
        validateAddressOwnership(request.getShippingAddressId(), "shipping", userId);
    }

    private void validateShippingValue(CreateOrderRequest request) {
        BigDecimal shippingValue = request.getShippingValue();
        if (shippingValue == null) {
            throw new CartValidationException("Shipping value is required");
        }
        if (shippingValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new CartValidationException("Shipping value must be greater than or equal to 0");
        }
        if (shippingValue.compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw new CartValidationException("Shipping value seems excessively high. Please verify the amount.");
        }
        log.debug("Validated shipping value: {}", shippingValue);
    }

    private void validateAddressOwnership(Long addressId, String addressType, Long userId) {
        try {
            AddressDTO address = userClient.getAddressById(addressId);
            if (!address.getUserId().equals(userId)) {
                throw new CartValidationException(String.format("%s address does not belong to current user", addressType));
            }
            log.debug("Validated {} address: {}", addressType, addressId);
        } catch (Exception e) {
            log.error("Failed to validate {} address {}: {}", addressType, addressId, e.getMessage());
            throw new CartValidationException(String.format("Invalid %s address: %s", addressType, addressId));
        }
    }

    private void validateOrderOwnership(Order order) {
        Long currentUserId = currentUserService.getCurrentUserId();
        if (!order.getUserId().equals(currentUserId.toString())) {
            throw new AccessDeniedException("Access denied to order: " + order.getId());
        }
    }

    private void validateOrderCanBeCancelled(Order order) {
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new CartValidationException("Order cannot be cancelled. Current status: " + order.getStatus());
        }
    }

    private void validateOrderCanBePaid(Order order) {
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new CartValidationException("Order cannot be paid. Current status: " + order.getStatus() + ". Only PENDING orders can be paid.");
        }
        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            throw new CartValidationException("Order is already paid");
        }
        if (order.getPaymentStatus() == Order.PaymentStatus.FAILED) {
            throw new CartValidationException("Order payment has failed. Please try again or contact support.");
        }
        if (order.getTotal() == null || order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CartValidationException("Order has invalid total amount: " + order.getTotal());
        }
    }

    // ============================================================================
    // CALCULATION HELPER METHODS
    // ============================================================================

    private OrderCalculation calculateOrderTotals(CartResponseDTO cart, String couponCode, BigDecimal shippingValue) {
        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal discounts = BigDecimal.ZERO;
        CouponDTO appliedCoupon = null;

        log.info("Calculating order totals - Cart ID: {}, Coupon Code: '{}', Subtotal: {}", 
                cart.getId(), couponCode, subtotal);

        // Simple coupon validation and discount calculation
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            try {
                appliedCoupon = couponClient.validateCoupon(couponCode);
                if (appliedCoupon != null) {
                    discounts = calculateDiscount(appliedCoupon, subtotal);
                    log.info("Coupon applied: {} - Discount: {}, Type: {}", 
                            couponCode, discounts, appliedCoupon.getType());
                }
            } catch (Exception e) {
                log.error("Coupon validation failed: {} - Error: {}", couponCode, e.getMessage());
                discounts = BigDecimal.ZERO;
                appliedCoupon = null;
            }
        }

        BigDecimal taxableAmount = subtotal.subtract(discounts).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = calculateTax(taxableAmount);
        BigDecimal total = calculateTotal(taxableAmount, tax, shippingValue);

        log.info("Final calculation - Subtotal: {}, Discounts: {}, Tax: {}, Shipping: {}, Total: {}", 
                subtotal, discounts, tax, shippingValue, total);

        return new OrderCalculation(subtotal, tax, discounts, shippingValue, total, appliedCoupon);
    }

    private BigDecimal calculateSubtotal(CartResponseDTO cart) {
        return cart.getItems().stream()
                .mapToDouble(CartItemResponseDTO::getSubtotal)
                .mapToObj(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTax(BigDecimal taxableAmount) {
        return taxableAmount.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotal(BigDecimal taxableAmount, BigDecimal tax, BigDecimal shippingValue) {
        BigDecimal total = taxableAmount.add(tax).add(shippingValue).setScale(2, RoundingMode.HALF_UP);
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total;
    }

    private BigDecimal calculateDiscount(CouponDTO coupon, BigDecimal totalAmount) {
        BigDecimal discount;
        switch (coupon.getType()) {
            case PERCENTAGE:
                discount = totalAmount.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
            case FIXED_AMOUNT:
                discount = coupon.getValue();
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    // ============================================================================
    // ENTITY MANAGEMENT HELPER METHODS
    // ============================================================================

    private Order createOrderEntity(CreateOrderRequest request, Long userId, CartResponseDTO cart, 
                                  String orderNumber, OrderCalculation calculation, BigDecimal roundedShippingValue) {
        return Order.builder()
                .orderNumber(orderNumber)
                .userId(userId.toString())
                .cartId(cart.getId())
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .subtotal(calculation.getSubtotal())
                .tax(calculation.getTax())
                .discounts(calculation.getDiscounts())
                .total(calculation.getTotal())
                .shippingAddressId(request.getShippingAddressId())
                .shippingValue(roundedShippingValue)
                .couponCode(request.getCouponCode())
                .discountType(calculation.getAppliedCoupon() != null ? calculation.getAppliedCoupon().getType().name() : null)
                .deliveryType(request.getDeliveryType() != null ? 
                    Order.DeliveryType.valueOf(request.getDeliveryType().toUpperCase()) : Order.DeliveryType.STANDARD)
                .shippingDate(request.getShippingDate())
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private List<OrderItem> createOrderItems(CartResponseDTO cart, Order savedOrder) {
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(savedOrder)
                        .productId(cartItem.getProductId())
                        .productName(cartItem.getProductName())
                        .quantity(cartItem.getQuantity())
                        .productPrice(BigDecimal.valueOf(cartItem.getPrice()))
                        .subtotal(BigDecimal.valueOf(cartItem.getSubtotal()))
                        .build())
                .collect(Collectors.toList());
        
        log.info("Created {} order items from cart for order {}: {}", 
                orderItems.size(), savedOrder.getId(), 
                orderItems.stream()
                    .map(item -> String.format("Product %d (Qty: %d, Price: %s)", 
                        item.getProductId(), item.getQuantity(), item.getProductPrice()))
                    .collect(Collectors.joining(", ")));
        
        return orderItems;
    }

    private void createOrderStatusHistory(Order order, String notes) {
        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .notes(notes)
                .createdBy("ORDER_SERVICE")
                .createdAt(LocalDateTime.now())
                .build());
    }

    // ============================================================================
    // UTILITY HELPER METHODS
    // ============================================================================



    private void handleCouponChanges(Order existingOrder, CartResponseDTO cart, CreateOrderRequest request) {
        String currentCouponCode = existingOrder.getCouponCode();
        String newCouponCode = request.getCouponCode();
        
        log.info("Handling coupon changes for order {}: current={}, new={}", 
                existingOrder.getId(), currentCouponCode, newCouponCode);
        
        // If coupon code was sent, validate it
        if (newCouponCode != null && !newCouponCode.trim().isEmpty()) {
            try {
                CouponDTO coupon = couponClient.validateCoupon(newCouponCode);
                if (coupon != null) {
                    log.info("New coupon {} validated for order {}", newCouponCode, existingOrder.getId());
                }
            } catch (Exception e) {
                log.warn("Coupon validation failed: {} - Error: {}", newCouponCode, e.getMessage());
            }
        } else {
            // No coupon code sent, remove any existing discount
            if (currentCouponCode != null && !currentCouponCode.trim().isEmpty()) {
                log.info("Coupon removed from order {}: {}", existingOrder.getId(), currentCouponCode);
            }
        }
    }

    private boolean isSameCoupon(String currentCoupon, String newCoupon) {
        if (currentCoupon == null && newCoupon == null) return true;
        if (currentCoupon == null || newCoupon == null) return false;
        return currentCoupon.trim().equals(newCoupon.trim());
    }

    private void updateCartStatus(Long cartId, Long orderId) {
        try {
            cartClient.markCartAsConverted(orderId);
            log.info("Updated cart status to CONVERTED for cart: {} with order: {}", cartId, orderId);
        } catch (Exception e) {
            log.error("Failed to update cart status to CONVERTED for cart {} with order {}: {}", cartId, orderId, e.getMessage());
            throw createCartValidationException(e);
        }
    }

    private void markCouponAsUsed(String couponCode, Long orderId) {
        try {
            CouponClient.UseCouponRequest couponRequest = new CouponClient.UseCouponRequest(couponCode, orderId);
            couponClient.useCoupon(couponRequest);
            log.info("Marked coupon {} as used for order {}", couponCode, orderId);
        } catch (Exception e) {
            log.error("Failed to mark coupon {} as used: {}", couponCode, e.getMessage());
        }
    }

    private void markCouponAsUsedIfApplied(Order order) {
        if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
            if (order.getDiscounts() != null && order.getDiscounts().compareTo(BigDecimal.ZERO) > 0) {
                markCouponAsUsed(order.getCouponCode(), order.getId());
                log.info("Marked coupon {} as used for order {} during payment completion (discount: {})", 
                        order.getCouponCode(), order.getId(), order.getDiscounts());
            } else {
                log.warn("Order {} has coupon code {} but no discount amount - coupon not marked as used", 
                        order.getId(), order.getCouponCode());
            }
        } else {
            log.debug("No coupon applied to order {} - no action needed", order.getId());
        }
    }

    // ============================================================================
    // DTO BUILDING HELPER METHODS
    // ============================================================================

    private OrderResponseDTO buildOrderResponseDTO(Order order, List<OrderItem> orderItems, OrderCalculation calculation) {
        List<OrderItem> items = getOrderItemsSafely(order, orderItems);
        List<OrderItemDTO> orderItemDTOs = buildOrderItemDTOs(items);
        OrderCouponDTO couponDTO = buildCouponDTO(calculation);

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(Long.valueOf(order.getUserId()))
                .cartId(order.getCartId())
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .discounts(order.getDiscounts())
                .total(order.getTotal())
                .shippingValue(order.getShippingValue())
                .shippingAddressId(order.getShippingAddressId())
                .deliveryType(order.getDeliveryType() != null ? order.getDeliveryType().name() : null)
                .shippingDate(order.getShippingDate())
                .notes(order.getNotes())
                .items(orderItemDTOs)
                .appliedCoupon(couponDTO)
                .couponCode(order.getCouponCode())
                .discountType(order.getDiscountType())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private List<OrderItem> getOrderItemsSafely(Order order, List<OrderItem> orderItems) {
        if (orderItems != null) {
            return orderItems;
        }
        try {
            return orderItemRepository.findByOrderId(order.getId());
        } catch (Exception e) {
            log.warn("Failed to retrieve order items for order {}: {}", order.getId(), e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<OrderItemDTO> buildOrderItemDTOs(List<OrderItem> items) {
        return items.stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getProductPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
    }

    private OrderCouponDTO buildCouponDTO(OrderCalculation calculation) {
        if (calculation == null || calculation.getAppliedCoupon() == null) {
            return null;
        }
        return OrderCouponDTO.builder()
                .couponCode(calculation.getAppliedCoupon().getCode())
                .couponName(calculation.getAppliedCoupon().getName())
                .discountAmount(calculation.getDiscounts())
                .discountType(calculation.getAppliedCoupon().getType().name())
                .appliedAt(LocalDateTime.now())
                .build();
    }

    // ============================================================================
    // DATA ACCESS HELPER METHODS
    // ============================================================================

    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    private Order findOrderByCartId(Long cartId) {
        return orderRepository.findByCartId(cartId).orElse(null);
    }

    // ============================================================================
    // ERROR HANDLING HELPER METHODS
    // ============================================================================

    private CartValidationException createCartValidationException(Exception e) {
        if (e.getMessage().contains("404") || e.getMessage().contains("Cart not found")) {
            return new CartValidationException("No active cart found. Please add items to your cart before creating an order.");
        } else if (e.getMessage().contains("Connection refused")) {
            return new CartValidationException("Cannot connect to cart service. Service may not be running.");
        } else {
            return new CartValidationException("Failed to retrieve cart: " + e.getMessage());
        }
    }

    // ============================================================================
    // INNER CLASSES
    // ============================================================================

    // Helper class for order calculations
    private static class OrderCalculation {
        private final BigDecimal subtotal;
        private final BigDecimal tax;
        private final BigDecimal discounts;
        private final BigDecimal shippingValue;
        private final BigDecimal total;
        private final CouponDTO appliedCoupon;

        public OrderCalculation(BigDecimal subtotal, BigDecimal tax, 
                              BigDecimal discounts, BigDecimal shippingValue, BigDecimal total, CouponDTO appliedCoupon) {
            this.subtotal = subtotal;
            this.tax = tax;
            this.discounts = discounts;
            this.shippingValue = shippingValue;
            this.total = total;
            this.appliedCoupon = appliedCoupon;
        }

        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getTax() { return tax; }
        public BigDecimal getDiscounts() { return discounts; }
        public BigDecimal getShippingValue() { return shippingValue; }
        public BigDecimal getTotal() { return total; }
        public CouponDTO getAppliedCoupon() { return appliedCoupon; }
    }
}
