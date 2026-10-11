package com.mankind.matrix_product_service.service;

import com.mankind.api.product.dto.recentlyviewed.RecentlyViewedProductResponseDTO;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.exception.InvalidOperationException;
import com.mankind.matrix_product_service.mapper.RecentlyViewedProductMapper;
import com.mankind.matrix_product_service.model.Product;
import com.mankind.matrix_product_service.model.RecentlyViewedProduct;
import com.mankind.matrix_product_service.repository.ProductRepository;
import com.mankind.matrix_product_service.repository.RecentlyViewedProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentlyViewedProductService {

    private final RecentlyViewedProductRepository recentlyViewedProductRepository;
    private final ProductRepository productRepository;
    private final RecentlyViewedProductMapper recentlyViewedProductMapper;

    @Value("${app.recently-viewed.max-items:10}")
    private int maxRecentlyViewedItems;

    /**
     * Add or update a product in a user's recently viewed products list
     * 
     * @param userId the ID of the user
     * @param productId the ID of the product
     * @return the updated recently viewed product entry
     */
    @Transactional
    public RecentlyViewedProductResponseDTO addRecentlyViewedProduct(Long userId, Long productId) {
        try {
            // Validate user ID
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("Invalid user ID: " + userId);
            }

            // Validate product ID
            if (productId == null || productId <= 0) {
                throw new IllegalArgumentException("Invalid product ID: " + productId);
            }

            // Validate product exists and is active
            Product product = productRepository.findByIdAndIsActiveTrue(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            LocalDateTime now = LocalDateTime.now();

            // Check if product is already in recently viewed
            Optional<RecentlyViewedProduct> existingEntry = recentlyViewedProductRepository.findByUserIdAndProductId(userId, productId);

            if (existingEntry.isPresent()) {
                try {
                    // Update last viewed time
                    RecentlyViewedProduct entry = existingEntry.get();
                    entry.setLastViewedAt(now);
                    return recentlyViewedProductMapper.toDto(recentlyViewedProductRepository.save(entry));
                } catch (Exception e) {
                    log.error("Error updating recently viewed product for user ID: {} and product ID: {}", userId, productId, e);
                    throw new InvalidOperationException("Failed to update recently viewed product: " + e.getMessage());
                }
            } else {
                try {
                    // Create new entry
                    RecentlyViewedProduct newEntry = new RecentlyViewedProduct();
                    newEntry.setUserId(userId);
                    newEntry.setProductId(productId);
                    newEntry.setProduct(product);
                    newEntry.setViewedAt(now);
                    newEntry.setLastViewedAt(now);

                    // Check if user has reached the maximum number of recently viewed items
                    long count = recentlyViewedProductRepository.countByUserId(userId);
                    if (count >= maxRecentlyViewedItems) {
                        try {
                            // Remove oldest entry
                            recentlyViewedProductRepository.findOldestByUserId(userId)
                                    .ifPresent(oldest -> recentlyViewedProductRepository.delete(oldest));
                        } catch (Exception e) {
                            log.warn("Error removing oldest recently viewed product for user ID: {}", userId, e);
                            // Continue with adding new entry even if removing oldest fails
                        }
                    }

                    return recentlyViewedProductMapper.toDto(recentlyViewedProductRepository.save(newEntry));
                } catch (Exception e) {
                    log.error("Error adding recently viewed product for user ID: {} and product ID: {}", userId, productId, e);
                    throw new InvalidOperationException("Failed to add recently viewed product: " + e.getMessage());
                }
            }
        } catch (ResourceNotFoundException | IllegalArgumentException | InvalidOperationException e) {
            // Let these exceptions propagate as they will be handled by the GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in addRecentlyViewedProduct for user ID: {} and product ID: {}", userId, productId, e);
            throw new InvalidOperationException("An unexpected error occurred while processing your request");
        }
    }

    /**
     * Get a user's recently viewed products
     * 
     * @param userId the ID of the user
     * @param pageable pagination information
     * @return a page of recently viewed products
     */
    @Transactional(readOnly = true)
    public Page<RecentlyViewedProductResponseDTO> getRecentlyViewedProducts(Long userId, Pageable pageable) {
        try {
            // Validate user ID
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("Invalid user ID: " + userId);
            }

            // Validate pageable
            if (pageable == null) {
                throw new IllegalArgumentException("Pagination information cannot be null");
            }

            // Validate sort properties
            if (pageable.getSort() != null && pageable.getSort().isSorted()) {
                validateSortProperties(pageable);
            }

            try {
                Page<RecentlyViewedProduct> recentlyViewedProducts = recentlyViewedProductRepository.findByUserIdOrderByLastViewedAtDesc(userId, pageable);
                return recentlyViewedProducts.map(recentlyViewedProductMapper::toDto);
            } catch (Exception e) {
                log.error("Error retrieving recently viewed products for user ID: {}", userId, e);
                throw new InvalidOperationException("Failed to retrieve recently viewed products: " + e.getMessage());
            }
        } catch (IllegalArgumentException | InvalidOperationException e) {
            // Let these exceptions propagate as they will be handled by the GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getRecentlyViewedProducts for user ID: {}", userId, e);
            throw new InvalidOperationException("An unexpected error occurred while processing your request");
        }
    }

    /**
     * Validate that the sort properties in the pageable object exist in the RecentlyViewedProduct entity
     * 
     * @param pageable the pageable object containing sort properties
     * @throws InvalidOperationException if an invalid sort property is found
     */
    private void validateSortProperties(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            if (!isValidSortProperty(property)) {
                throw new InvalidOperationException("Invalid sort property: '" + property + "'. Valid properties are: 'id', 'userId', 'productId', 'viewedAt', 'lastViewedAt'");
            }
        });
    }

    /**
     * Check if a property is a valid sort property for the RecentlyViewedProduct entity
     * 
     * @param property the property to check
     * @return true if the property is valid, false otherwise
     */
    private boolean isValidSortProperty(String property) {
        return property.equals("id") || 
               property.equals("userId") || 
               property.equals("productId") || 
               property.equals("viewedAt") || 
               property.equals("lastViewedAt");
    }

    /**
     * Remove a product from a user's recently viewed products
     * 
     * @param userId the ID of the user
     * @param productId the ID of the product
     */
    @Transactional
    public void removeRecentlyViewedProduct(Long userId, Long productId) {
        try {
            // Validate user ID
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("Invalid user ID: " + userId);
            }

            // Validate product ID
            if (productId == null || productId <= 0) {
                throw new IllegalArgumentException("Invalid product ID: " + productId);
            }

            // Check if the product exists in the user's recently viewed products
            if (!recentlyViewedProductRepository.existsByUserIdAndProductId(userId, productId)) {
                throw new ResourceNotFoundException("Product not found in user's recently viewed products");
            }

            try {
                recentlyViewedProductRepository.deleteByUserIdAndProductId(userId, productId);
                log.info("Successfully removed product ID: {} from user ID: {}'s recently viewed products", productId, userId);
            } catch (Exception e) {
                log.error("Error removing product ID: {} from user ID: {}'s recently viewed products", productId, userId, e);
                throw new InvalidOperationException("Failed to remove product from recently viewed: " + e.getMessage());
            }
        } catch (ResourceNotFoundException | IllegalArgumentException | InvalidOperationException e) {
            // Let these exceptions propagate as they will be handled by the GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in removeRecentlyViewedProduct for user ID: {} and product ID: {}", userId, productId, e);
            throw new InvalidOperationException("An unexpected error occurred while processing your request");
        }
    }

    /**
     * Clear all recently viewed products for a user
     * 
     * @param userId the ID of the user
     */
    @Transactional
    public void clearRecentlyViewedProducts(Long userId) {
        try {
            // Validate user ID
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("Invalid user ID: " + userId);
            }

            try {
                Page<RecentlyViewedProduct> recentlyViewedProducts = recentlyViewedProductRepository.findByUserIdOrderByLastViewedAtDesc(userId, Pageable.unpaged());

                if (recentlyViewedProducts.isEmpty()) {
                    throw new ResourceNotFoundException("No recently viewed products found for user with ID: " + userId);
                }

                try {
                    recentlyViewedProducts.forEach(recentlyViewedProductRepository::delete);
                    log.info("Successfully cleared all recently viewed products for user ID: {}", userId);
                } catch (Exception e) {
                    log.error("Error clearing recently viewed products for user ID: {}", userId, e);
                    throw new InvalidOperationException("Failed to clear recently viewed products: " + e.getMessage());
                }
            } catch (ResourceNotFoundException e) {
                // Let ResourceNotFoundException propagate as it will be handled by the GlobalExceptionHandler
                throw e;
            } catch (Exception e) {
                log.error("Error retrieving recently viewed products for user ID: {}", userId, e);
                throw new InvalidOperationException("Failed to retrieve recently viewed products: " + e.getMessage());
            }
        } catch (ResourceNotFoundException | IllegalArgumentException | InvalidOperationException e) {
            // Let these exceptions propagate as they will be handled by the GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in clearRecentlyViewedProducts for user ID: {}", userId, e);
            throw new InvalidOperationException("An unexpected error occurred while processing your request");
        }
    }
}
