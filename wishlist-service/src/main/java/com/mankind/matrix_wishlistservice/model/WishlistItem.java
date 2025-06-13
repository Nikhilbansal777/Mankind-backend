package com.mankind.matrix_wishlistservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wishlist", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "productId"}))

public class WishlistItem {

	public WishlistItem() {
    }

	public WishlistItem(Long id, Long userId, Long productId, String name, String brand, BigDecimal price, String imageUrl) {
	    this.id = id;
	    this.userId = userId;
	    this.productId = productId;
	    this.name = name;
	    this.brand = brand;
	    this.price = price;
	    this.imageUrl = imageUrl;
	    this.discountedPrice = price; // Default to regular price
	    this.rating = 0.0f; // Default rating
	    this.reviewCount = 0; // Default review count
	}

	public WishlistItem(Long id, Long userId, Long productId, String name, String brand, BigDecimal price, 
	                    BigDecimal discountedPrice, String imageUrl, Float rating, Integer reviewCount) {
	    this.id = id;
	    this.userId = userId;
	    this.productId = productId;
	    this.name = name;
	    this.brand = brand;
	    this.price = price;
	    this.discountedPrice = discountedPrice;
	    this.imageUrl = imageUrl;
	    this.rating = rating;
	    this.reviewCount = reviewCount;
	}

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
		@JsonIgnore
	    private Long id;

	    private Long userId;

	    private Long productId;

	    private String name;

	    private String brand;

	    private BigDecimal price;

	    private BigDecimal discountedPrice;

	    private String imageUrl;

	    private Float rating;

	    private Integer reviewCount;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Long getProductId() {
			return productId;
		}

		public void setProductId(Long productId) {
			this.productId = productId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getBrand() {
			return brand;
		}

		public void setBrand(String brand) {
			this.brand = brand;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

 	public String getImageUrl() {
 		return imageUrl;
 	}

 	public void setImageUrl(String imageUrl) {
 		this.imageUrl = imageUrl;
 	}

 	public BigDecimal getDiscountedPrice() {
 		return discountedPrice;
 	}

 	public void setDiscountedPrice(BigDecimal discountedPrice) {
 		this.discountedPrice = discountedPrice;
 	}

 	public Float getRating() {
 		return rating;
 	}

 	public void setRating(Float rating) {
 		this.rating = rating;
 	}

 	public Integer getReviewCount() {
 		return reviewCount;
 	}

 	public void setReviewCount(Integer reviewCount) {
 		this.reviewCount = reviewCount;
 	}
 }
