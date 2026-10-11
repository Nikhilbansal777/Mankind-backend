package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.InventoryLog;
import com.mankind.matrix_product_service.model.InventoryLog.InventoryActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    Page<InventoryLog> findByInventoryId(Long inventoryId, Pageable pageable);
    List<InventoryLog> findByInventoryId(Long inventoryId);
    List<InventoryLog> findByInventoryIdAndActionType(Long inventoryId, InventoryActionType actionType);
} 