package com.mankind.matrix_cart_service.service;

import com.mankind.matrix_cart_service.client.ProductClient;
import com.mankind.matrix_cart_service.dto.CartResponseDTO;
import com.mankind.matrix_cart_service.mapper.CartItemMapper;
import com.mankind.matrix_cart_service.mapper.CartMapper;
import com.mankind.matrix_cart_service.model.Cart;
import com.mankind.matrix_cart_service.model.CartStatus;
import com.mankind.matrix_cart_service.repository.CartItemRepository;
import com.mankind.matrix_cart_service.repository.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartService cartService;

    @Test
    void getCurrentUserOpenCart_shouldReturnNull_whenCartDoesNotExist() {
        // Arrange
        Long userId = 101L;

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserIdAndStatus(
                userId,
                CartStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // Act
        CartResponseDTO result = cartService.getCurrentUserOpenCart();

        // Assert
        assertThat(result).isNull();

        verify(currentUserService).getCurrentUserId();
        verify(cartRepository)
                .findByUserIdAndStatus(userId, CartStatus.ACTIVE);

        verifyNoInteractions(cartMapper);
        verifyNoInteractions(productClient);
    }

    @Test
    void markCartAsConverted_shouldUpdateCartStatus() {
        // Arrange
        Long userId = 101L;
        Long orderId = 5001L;

        Cart cart = Cart.builder()
                .userId(userId)
                .status(CartStatus.ACTIVE)
                .cartItems(new ArrayList<>())
                .build();

        CartResponseDTO responseDTO = mock(CartResponseDTO.class);

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserIdAndStatus(
                userId,
                CartStatus.ACTIVE
        )).thenReturn(Optional.of(cart));

        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartMapper.toResponseDTO(cart)).thenReturn(responseDTO);

        // Act
        CartResponseDTO result = cartService.markCartAsConverted(orderId);

        // Assert
        assertThat(result).isSameAs(responseDTO);
        assertThat(cart.getStatus()).isEqualTo(CartStatus.CONVERTED);

        verify(cartRepository).save(cart);
        verify(cartMapper).toResponseDTO(cart);
    }

    @Test
    void markCartAsConverted_shouldThrowException_whenActiveCartDoesNotExist() {
        // Arrange
        Long userId = 101L;
        Long orderId = 5001L;

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserIdAndStatus(
                userId,
                CartStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() ->
                cartService.markCartAsConverted(orderId)
        )
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No active cart found for user");

        verify(cartRepository, never()).save(any());
        verifyNoInteractions(cartMapper);
    }
}