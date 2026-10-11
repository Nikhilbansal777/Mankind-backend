package com.mankind.matrix_product_service.service;

import com.mankind.api.product.dto.product.ProductDTO;
import com.mankind.api.product.dto.product.ProductResponseDTO;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.mapper.ProductMapper;
import com.mankind.matrix_product_service.model.Product;
import com.mankind.matrix_product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RoleVerificationService roleVerificationService;

    /**
     * Verify if the current user has ADMIN or SUPER_ADMIN role
     * @throws ResponseStatusException if user doesn't have required role
     */


    @Transactional
    public ProductResponseDTO createProduct(ProductDTO productDTO) {
        // Verify admin role for product creation
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        if (productRepository.existsByNameAndCategoryId(productDTO.getName(), productDTO.getCategoryId())) {
            throw new IllegalStateException("Product name already exists in this category");
        }

        Product product = productMapper.toEntity(productDTO);
        product.setActive(true);
        product.setFeatured(productDTO.getIsFeatured() != null ? productDTO.getIsFeatured() : false);
        product = productRepository.save(product);

        return productMapper.toResponseDTO(product);
    }

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable)
                .map(productMapper::toResponseDTO);
    }

    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findByIdAndIsActiveTrue(id)
                .map(productMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Page<ProductResponseDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                .map(productMapper::toResponseDTO);
    }

    public Page<ProductResponseDTO> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable)
                .map(productMapper::toResponseDTO);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductDTO productDTO) {
        // Verify admin role for product updates
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        // Validate input
        if (productDTO == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        // Retrieve the product
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Only update name if it's provided in the request
        if (productDTO.getName() != null) {
            String newName = productDTO.getName().trim();
            if (newName.isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            
            // Only check for name uniqueness if the name is actually being changed
            if (!product.getName().equals(newName)) {
                Long categoryId = productDTO.getCategoryId() != null ? 
                    productDTO.getCategoryId() : product.getCategoryId();
                
                if (productRepository.existsByNameAndCategoryIdAndIdNot(newName, categoryId, id)) {
                    throw new IllegalStateException("Product name already exists in this category");
                }
            }
            product.setName(newName);
        }

        // Only update category if it's provided
        if (productDTO.getCategoryId() != null) {
            // If name is not being updated, check if the new category would cause a name conflict
            if (productDTO.getName() == null && 
                productRepository.existsByNameAndCategoryIdAndIdNot(product.getName(), productDTO.getCategoryId(), id)) {
                throw new IllegalStateException("Product name already exists in the target category");
            }
            product.setCategoryId(productDTO.getCategoryId());
        }

        // Only update other fields if they are provided in the request
        if (productDTO.getDescription() != null) {
            validateDescription(productDTO.getDescription().trim());
            product.setDescription(productDTO.getDescription().trim());
        }
        if (productDTO.getSku() != null) {
            validateSku(productDTO.getSku().trim(), product.getId());
            product.setSku(productDTO.getSku().trim());
        }
        if (productDTO.getBrand() != null) {
            validateBrand(productDTO.getBrand().trim());
            product.setBrand(productDTO.getBrand().trim());
        }
        if (productDTO.getModel() != null) {
            validateModel(productDTO.getModel().trim());
            product.setModel(productDTO.getModel().trim());
        }
        if (productDTO.getSpecifications() != null) {
            validateSpecifications(productDTO.getSpecifications());
            if (product.getSpecifications() == null) {
                product.setSpecifications(new HashMap<>());
            }
            product.getSpecifications().clear();
            product.getSpecifications().putAll(productDTO.getSpecifications());
        }
        if (productDTO.getImages() != null) {
            validateImages(productDTO.getImages());
            if (product.getImages() == null) {
                product.setImages(new ArrayList<>());
            }
            product.getImages().clear();
            product.getImages().addAll(productDTO.getImages());
        }

        // Update featured status if provided
        if (productDTO.getIsFeatured() != null) {
            product.setFeatured(productDTO.getIsFeatured());
        }

        // Save and return
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO toggleFeaturedStatus(Long id) {
        // Verify admin role for featured status changes
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        product.setFeatured(!product.isFeatured());
        return productMapper.toResponseDTO(productRepository.save(product));
    }

    private void validateProductName(String name, Long categoryId, Long productId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Product name cannot exceed 100 characters");
        }
        if (productRepository.existsByNameAndCategoryIdAndIdNot(name, categoryId, productId)) {
            throw new IllegalStateException("Product name already exists in this category");
        }
    }

    private void validateCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        // Add category existence validation if you have a category service/repository
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 2000) {
            throw new IllegalArgumentException("Description cannot exceed 2000 characters");
        }
    }

    private void validateSku(String sku, Long productId) {
        if (sku != null) {
            if (sku.length() > 50) {
                throw new IllegalArgumentException("SKU cannot exceed 50 characters");
            }
            // Add SKU uniqueness validation if needed
        }
    }

    private void validateBrand(String brand) {
        if (brand != null && brand.length() > 50) {
            throw new IllegalArgumentException("Brand cannot exceed 50 characters");
        }
    }

    private void validateModel(String model) {
        if (model != null && model.length() > 50) {
            throw new IllegalArgumentException("Model cannot exceed 50 characters");
        }
    }

    private void validateSpecifications(Map<String, String> specifications) {
        if (specifications != null) {
            specifications.forEach((key, value) -> {
                if (key == null || key.trim().isEmpty()) {
                    throw new IllegalArgumentException("Specification key cannot be empty");
                }
                if (value != null && value.length() > 255) {
                    throw new IllegalArgumentException("Specification value cannot exceed 255 characters");
                }
            });
        }
    }

    private void validateImages(List<String> images) {
        if (images != null) {
            for (String image : images) {
                if (image != null && image.length() > 255) {
                    throw new IllegalArgumentException("Image URL cannot exceed 255 characters");
                }
            }
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        // Verify admin role for product deletion
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setActive(false);
        productRepository.save(product);
    }
}

