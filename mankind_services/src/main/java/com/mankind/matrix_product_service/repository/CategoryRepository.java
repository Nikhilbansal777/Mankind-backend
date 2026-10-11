package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIdIsNull();
    List<Category> findByParentId(Long parentId);
    
    // Check for duplicate names at the same level (same parent)
    Optional<Category> findByNameAndParentId(String name, Long parentId);
    
    // Check if name exists at root level
    boolean existsByNameAndParentIdIsNull(String name);
    
    // Check if name exists under a specific parent
    boolean existsByNameAndParentId(String name, Long parentId);
} 