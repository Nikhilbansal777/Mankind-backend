package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    
    @Query("SELECT i FROM Inventory i LEFT JOIN FETCH i.logs WHERE i.product.id = :productId")
    Optional<Inventory> findByProductIdWithLogs(Long productId);
} 