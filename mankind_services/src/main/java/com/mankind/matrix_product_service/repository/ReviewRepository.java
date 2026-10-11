package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    List<Review> findByProductIdAndUserId(Long productId, Long userId);
    List<Review> findTop100ByProductIdOrderByCreatedAtDesc(Long productId);
    Page<Review> findByProductId(Long productId, Pageable pageable);
    Page<Review> findByUserId(Long userId, Pageable pageable);
} 