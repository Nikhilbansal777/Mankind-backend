package com.mankind.matrix_product_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventories")
@EntityListeners(AuditingEntityListener.class)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private BigDecimal availableQuantity;

    @Column(nullable = false)
    private BigDecimal reservedQuantity;

    @Column(nullable = false)
    private BigDecimal soldQuantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "corporate_price", precision = 10, scale = 2)
    private BigDecimal corporatePrice;

    @Column(name = "updated_by_corporate")
    private String updatedByCorporate;

    @Column(name = "last_updated_at_corporate")
    private LocalDateTime lastUpdatedAtCorporate;

    @Column(length = 3, nullable = false)
    private String currency;

    @Column(name = "max_quantity_per_purchase")
    private BigDecimal maxQuantityPerPurchase;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryLog> logs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (availableQuantity == null) {
            availableQuantity = BigDecimal.ZERO;
        }
        if (reservedQuantity == null) {
            reservedQuantity = BigDecimal.ZERO;
        }
        if (soldQuantity == null) {
            soldQuantity = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
