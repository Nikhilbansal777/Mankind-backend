package com.mankind.matrix_wishlistservice.service;

import com.mankind.matrix_wishlistservice.exception.DuplicateWishlistItemException;
import com.mankind.matrix_wishlistservice.exception.ItemNotInWishlistException;
import com.mankind.matrix_wishlistservice.exception.UserNotFoundException;
import com.mankind.matrix_wishlistservice.model.WishlistItem;
import com.mankind.matrix_wishlistservice.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository repository;

    public WishlistService(WishlistRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WishlistItem addItem(Long userId, Long productId, String name, String brand, java.math.BigDecimal price, String imageUrl) {
        repository.findByUserIdAndProductId(userId, productId)
                .ifPresent(item -> {
                    throw new DuplicateWishlistItemException("Item already in wishlist");
                });

        WishlistItem item = new WishlistItem(null, userId, productId, name, brand, price, imageUrl);
        return repository.save(item);
    }

    @Transactional
    public WishlistItem addItem(Long userId, Long productId) {
        // For backward compatibility
        return addItem(userId, productId, null, null, null, null);
    }

    public List<WishlistItem> getUserWishlist(Long userId) {
        List<WishlistItem> wishlist = repository.findByUserId(userId);
        if (wishlist.isEmpty()) {
            throw new UserNotFoundException("User not found or no wishlist items available for user " + userId);
        }
        return wishlist;
    }

    @Transactional
    public void removeItem(Long userId, Long productId) {
        // Check if user has any wishlist items
        List<WishlistItem> userWishlist = repository.findByUserId(userId);
        if (userWishlist.isEmpty()) {
            throw new UserNotFoundException("User not found or no wishlist items available for user " + userId);
        }

        // Check if the specific product is in the user's wishlist
        repository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ItemNotInWishlistException("Product " + productId + " not found in wishlist for user " + userId));

        repository.deleteByUserIdAndProductId(userId, productId);
    }

    public boolean isInWishlist(Long userId, Long productId) {
        // Check if user has any wishlist items
        List<WishlistItem> userWishlist = repository.findByUserId(userId);
        if (userWishlist.isEmpty()) {
            throw new UserNotFoundException("User not found or no wishlist items available for user " + userId);
        }

        // Check if the specific product is in the user's wishlist
        boolean isPresent = repository.findByUserIdAndProductId(userId, productId).isPresent();
        if (!isPresent) {
            throw new ItemNotInWishlistException("Product " + productId + " not found in wishlist for user " + userId);
        }

        return true;
    }
}
