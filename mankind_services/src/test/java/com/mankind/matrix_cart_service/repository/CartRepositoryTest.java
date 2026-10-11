package com.mankind.matrix_cart_service.repository;

import com.mankind.matrix_cart_service.model.Cart;
import com.mankind.matrix_cart_service.model.CartItem;
import com.mankind.matrix_cart_service.model.CartStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void testCreateCart() {
        // Create a cart
        Cart cart = new Cart();
        cart.setUserId(101L);
        cart.setStatus(CartStatus.ACTIVE);

        // Save the cart
        Cart savedCart = entityManager.persistAndFlush(cart);

        // Verify the cart was saved
        assertThat(savedCart.getId()).isNotNull();
        assertThat(savedCart.getUserId()).isEqualTo(101L);
        assertThat(savedCart.getStatus()).isEqualTo(CartStatus.ACTIVE);
        assertThat(savedCart.getCreatedAt()).isNotNull();
        assertThat(savedCart.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testFindByUserIdAndStatus() {
        // Create a cart
        Cart cart = new Cart();
        cart.setUserId(102L);
        cart.setStatus(CartStatus.ACTIVE);
        entityManager.persistAndFlush(cart);

        // Find the cart
        Optional<Cart> foundCart = cartRepository.findByUserIdAndStatus(102L, CartStatus.ACTIVE);

        // Verify the cart was found
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUserId()).isEqualTo(102L);
        assertThat(foundCart.get().getStatus()).isEqualTo(CartStatus.ACTIVE);
    }

    @Test
    public void testFindBySessionIdAndStatus() {
        // Create a cart
        Cart cart = new Cart();
        cart.setSessionId("test-session-123");
        cart.setStatus(CartStatus.ACTIVE);
        entityManager.persistAndFlush(cart);

        // Find the cart
        Optional<Cart> foundCart = cartRepository.findBySessionIdAndStatus("test-session-123", CartStatus.ACTIVE);

        // Verify the cart was found
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getSessionId()).isEqualTo("test-session-123");
        assertThat(foundCart.get().getStatus()).isEqualTo(CartStatus.ACTIVE);
    }

    @Test
    public void testCartItemRelationship() {
        // Create a cart
        Cart cart = new Cart();
        cart.setUserId(103L);
        cart.setStatus(CartStatus.ACTIVE);
        Cart savedCart = entityManager.persistAndFlush(cart);

        // Create a cart item
        CartItem cartItem = new CartItem();
        cartItem.setCart(savedCart);
        cartItem.setProductId(201L);
        cartItem.setQuantity(2);
        cartItem.setPrice(19.99);
        CartItem savedCartItem = entityManager.persistAndFlush(cartItem);

        // Find the cart with items
        Cart foundCart = cartRepository.findById(savedCart.getId()).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findByCart(foundCart);

        // Verify the relationship
        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.get(0).getProductId()).isEqualTo(201L);
        assertThat(cartItems.get(0).getQuantity()).isEqualTo(2);
        assertThat(cartItems.get(0).getPrice()).isEqualTo(19.99);
    }

    @Test
    public void testUpdateCartStatus() {
        // Create a cart
        Cart cart = new Cart();
        cart.setUserId(104L);
        cart.setStatus(CartStatus.ACTIVE);
        Cart savedCart = entityManager.persistAndFlush(cart);

        // Update the cart status
        savedCart.setStatus(CartStatus.CONVERTED);
        entityManager.persistAndFlush(savedCart);

        // Find the cart
        Cart foundCart = cartRepository.findById(savedCart.getId()).orElseThrow();

        // Verify the status was updated
        assertThat(foundCart.getStatus()).isEqualTo(CartStatus.CONVERTED);
    }
}
