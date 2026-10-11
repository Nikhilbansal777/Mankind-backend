package com.mankind.matrix_cart_service.repository;

import com.mankind.matrix_cart_service.model.Cart;
import com.mankind.matrix_cart_service.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find all cart items for a specific cart
     * @param cart the cart
     * @return list of cart items
     */
    List<CartItem> findByCart(Cart cart);

    /**
     * Find all cart items for a specific cart ID
     * @param cartId the ID of the cart
     * @return list of cart items
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Find a specific cart item by cart and product ID
     * @param cart the cart
     * @param productId the ID of the product
     * @return the cart item if found
     */
    CartItem findByCartAndProductId(Cart cart, Long productId);

    /**
     * Find a specific cart item by cart ID and product ID
     * @param cartId the ID of the cart
     * @param productId the ID of the product
     * @return the cart item if found
     */
    CartItem findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Delete all cart items for a specific cart
     * @param cart the cart
     */
    void deleteByCart(Cart cart);

    /**
     * Delete all cart items for a specific cart ID
     * @param cartId the ID of the cart
     */
    void deleteByCartId(Long cartId);
}
