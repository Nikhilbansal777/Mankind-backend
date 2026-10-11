package com.mankind.matrix_cart_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(length = 100)
    private String sessionId; // For guest users

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CartStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional calculated fields
    private double subtotal;
    private double tax;
    private double total;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = CartStatus.ACTIVE;
        }
        // Initialize calculated values
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // Recalculate totals on update
        calculateTotals();
    }

    // Helper method to add cart item
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
        calculateTotals(); // Recalculate totals after adding item
    }

    // Helper method to remove cart item
    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
        calculateTotals(); // Recalculate totals after removing item
    }

    // Helper method to calculate totals
    public void calculateTotals() {
        // Calculate subtotal (sum of all items price * quantity)
        this.subtotal = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Calculate tax (assuming tax rate of 10%)
        this.tax = this.subtotal * 0.1;

        // Calculate total (subtotal + tax)
        this.total = this.subtotal + this.tax;
    }

    public Long getId() {
        return id;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }
}
