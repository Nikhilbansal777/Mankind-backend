package com.mankind.matrix_order_service.service;

import com.mankind.matrix_order_service.client.CartClient;
import com.mankind.matrix_order_service.client.CouponClient;
import com.mankind.matrix_order_service.client.PaymentClient;
import com.mankind.matrix_order_service.client.UserClient;
import com.mankind.matrix_order_service.dto.OrderResponseDTO;
import com.mankind.matrix_order_service.exception.AccessDeniedException;
import com.mankind.matrix_order_service.exception.CartValidationException;
import com.mankind.matrix_order_service.exception.OrderNotFoundException;
import com.mankind.matrix_order_service.model.Order;
import com.mankind.matrix_order_service.model.OrderItem;
import com.mankind.matrix_order_service.model.OrderStatusHistory;
import com.mankind.matrix_order_service.repository.OrderItemRepository;
import com.mankind.matrix_order_service.repository.OrderRepository;
import com.mankind.matrix_order_service.repository.OrderStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private CouponClient couponClient;

    @Mock
    private UserClient userClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private OrderNumberGenerator orderNumberGenerator;

    @InjectMocks
    private OrderService orderService;

    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        pendingOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-TEST-001")
                .userId("101")
                .cartId(10L)
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("10.00"))
                .discounts(BigDecimal.ZERO)
                .shippingValue(new BigDecimal("5.00"))
                .total(new BigDecimal("115.00"))
                .shippingAddressId(20L)
                .deliveryType(Order.DeliveryType.STANDARD)
                .notes("Test order")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findOrderById_WhenOrderExists_ReturnsOrder() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(pendingOrder));

        Order result = orderService.findOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ORD-TEST-001", result.getOrderNumber());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());

        verify(orderRepository).findById(1L);
    }

    @Test
    void findOrderById_WhenOrderDoesNotExist_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.findOrderById(99L)
        );

        assertTrue(exception.getMessage().contains("99"));

        verify(orderRepository).findById(99L);
    }

    @Test
    void getOrderById_WhenOrderBelongsToCurrentUser_ReturnsResponse() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(pendingOrder));
        when(currentUserService.getCurrentUserId())
                .thenReturn(101L);
        when(orderItemRepository.findByOrderId(1L))
                .thenReturn(Collections.emptyList());

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ORD-TEST-001", result.getOrderNumber());
        assertEquals(101L, result.getUserId());
        assertEquals("PENDING", result.getStatus());
        assertEquals("PENDING", result.getPaymentStatus());
        assertEquals(new BigDecimal("115.00"), result.getTotal());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        verify(orderRepository).findById(1L);
        verify(currentUserService).getCurrentUserId();
        verify(orderItemRepository).findByOrderId(1L);
    }

    @Test
    void getOrderById_WhenOrderBelongsToAnotherUser_ThrowsAccessDeniedException() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(pendingOrder));
        when(currentUserService.getCurrentUserId())
                .thenReturn(999L);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrderById(1L)
        );

        verify(orderRepository).findById(1L);
        verify(currentUserService).getCurrentUserId();
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void cancelOrder_WhenPendingOrderBelongsToUser_CancelsOrder() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(pendingOrder));
        when(currentUserService.getCurrentUserId())
                .thenReturn(101L);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.findByOrderId(1L))
                .thenReturn(Collections.emptyList());

        OrderResponseDTO result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        assertEquals(Order.OrderStatus.CANCELLED, pendingOrder.getStatus());
        assertNotNull(pendingOrder.getUpdatedAt());

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(pendingOrder);
        verify(orderStatusHistoryRepository)
                .save(any(OrderStatusHistory.class));
        verify(orderItemRepository).findByOrderId(1L);
    }

    @Test
    void cancelOrder_WhenOrderIsNotPending_ThrowsCartValidationException() {
        pendingOrder.setStatus(Order.OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(pendingOrder));
        when(currentUserService.getCurrentUserId())
                .thenReturn(101L);

        CartValidationException exception = assertThrows(
                CartValidationException.class,
                () -> orderService.cancelOrder(1L)
        );

        assertTrue(exception.getMessage().contains("cannot be cancelled"));

        verify(orderRepository).findById(1L);
        verify(currentUserService).getCurrentUserId();
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderStatusHistoryRepository, never())
                .save(any(OrderStatusHistory.class));
    }

    @Test
    void cancelOrder_WhenOrderBelongsToAnotherUser_ThrowsAccessDeniedException() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(pendingOrder));
        when(currentUserService.getCurrentUserId())
                .thenReturn(999L);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.cancelOrder(1L)
        );

        verify(orderRepository).findById(1L);
        verify(currentUserService).getCurrentUserId();
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderStatusHistoryRepository, never())
                .save(any(OrderStatusHistory.class));
    }
}