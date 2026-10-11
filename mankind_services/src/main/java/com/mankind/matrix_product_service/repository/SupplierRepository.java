package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByIdAndIsActiveTrue(Long id);
    Page<Supplier> findByIsActiveTrue(Pageable pageable);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    Optional<Supplier> findByEmailAndIsActiveTrue(String email);
}