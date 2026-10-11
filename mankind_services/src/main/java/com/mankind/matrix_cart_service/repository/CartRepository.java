package com.mankind.matrix_cart_service.repository;

import com.mankind.matrix_cart_service.model.Cart;
import com.mankind.matrix_cart_service.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Find active cart for a specific user
     * @param userId the ID of the user
     * @param status the status of the cart (usually ACTIVE)
     * @return the active cart if found
     */
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    
    /**
     * Find active cart for a guest session
     * @param sessionId the session ID
     * @param status the status of the cart (usually ACTIVE)
     * @return the active cart if found
     */
    Optional<Cart> findBySessionIdAndStatus(String sessionId, CartStatus status);
    
    /**
     * Find all carts for a specific user
     * @param userId the ID of the user
     * @return list of carts
     */
    List<Cart> findByUserId(Long userId);
    
    /**
     * Find all carts for a specific session
     * @param sessionId the session ID
     * @return list of carts
     */
    List<Cart> findBySessionId(String sessionId);
    
    /**
     * Find all carts with a specific status
     * @param status the status of the cart
     * @return list of carts
     */
    List<Cart> findByStatus(CartStatus status);
}