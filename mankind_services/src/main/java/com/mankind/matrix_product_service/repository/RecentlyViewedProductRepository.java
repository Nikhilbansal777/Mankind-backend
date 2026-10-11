package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.RecentlyViewedProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecentlyViewedProductRepository extends JpaRepository<RecentlyViewedProduct, Long> {
    
    /**
     * Find recently viewed products for a specific user, ordered by last viewed time (most recent first)
     */
    Page<RecentlyViewedProduct> findByUserIdOrderByLastViewedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find a specific recently viewed product by user ID and product ID
     */
    Optional<RecentlyViewedProduct> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Check if a product is in a user's recently viewed products
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Delete a specific recently viewed product by user ID and product ID
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Count the number of recently viewed products for a specific user
     */
    long countByUserId(Long userId);
    
    /**
     * Find the oldest recently viewed product for a specific user
     */
    @Query("SELECT r FROM RecentlyViewedProduct r WHERE r.userId = :userId ORDER BY r.lastViewedAt ASC LIMIT 1")
    Optional<RecentlyViewedProduct> findOldestByUserId(@Param("userId") Long userId);
}