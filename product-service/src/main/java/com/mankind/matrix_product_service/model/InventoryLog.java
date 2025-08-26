package com.mankind.matrix_product_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_logs")
public class InventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private InventoryActionType actionType;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "order_id")
    private Long orderId;

    public enum InventoryActionType {
        RESTOCK,
        SALE,
        RESERVATION,
        UNRESERVATION,
        PRICE_CHANGE,
        STOCK_ADJUSTMENT,
        CART_ADD,
        CART_UPDATE,
        CART_REMOVE,
        CART_CONVERT
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
} 