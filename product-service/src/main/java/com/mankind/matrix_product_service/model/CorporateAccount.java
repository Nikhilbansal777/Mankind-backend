package com.mankind.matrix_product_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "corporate_accounts")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CorporateAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "purchases", precision = 15, scale = 2)
    private BigDecimal purchases;

    @Column(name = "number_of_orders_placed", nullable = false)
    private Integer numberOfOrdersPlaced = 0;

    @Column(name = "date_of_joined")
    private LocalDateTime dateOfJoined;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
