package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndIsActiveTrue(Long id);
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    Page<Product> findByCategoryIdInAndIsActiveTrue(Iterable<Long> categoryIds, Pageable pageable);
    boolean existsByNameAndCategoryId(String name, Long categoryId);
    boolean existsByNameAndCategoryIdAndIdNot(String name, Long categoryId, Long id);
    Page<Product> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

    // Dashboard metrics for suppliers
    long countBySuppliers_Id(Long supplierId);
    long countBySuppliers_IdAndIsActiveTrue(Long supplierId);
    long countBySuppliers_IdAndIsActiveFalse(Long supplierId);
}

