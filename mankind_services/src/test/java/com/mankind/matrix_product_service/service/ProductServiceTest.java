package com.mankind.matrix_product_service.service;

import com.mankind.api.product.dto.product.ProductDTO;
import com.mankind.api.product.dto.product.ProductResponseDTO;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.mapper.ProductMapper;
import com.mankind.matrix_product_service.model.Product;
import com.mankind.matrix_product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @Mock private RoleVerificationService roleVerificationService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                productRepository, productMapper, roleVerificationService);
    }

    @Test
    void createProduct_ShouldCreateSuccessfully() {
        ProductDTO dto = productDTO();
        Product product = product();
        ProductResponseDTO response = new ProductResponseDTO();

        dto.setIsFeatured(true);
        when(productRepository.existsByNameAndCategoryId("Laptop", 1L)).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(response);

        ProductResponseDTO result = productService.createProduct(dto);

        assertSame(response, result);
        assertTrue(product.isActive());
        assertTrue(product.isFeatured());
        verify(roleVerificationService).verifyAdminOrSuperAdminRole();
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_ShouldDefaultFeaturedToFalse() {
        ProductDTO dto = productDTO();
        Product product = product();
        dto.setIsFeatured(null);

        when(productRepository.existsByNameAndCategoryId("Laptop", 1L)).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(new ProductResponseDTO());

        productService.createProduct(dto);

        assertFalse(product.isFeatured());
    }

    @Test
    void createProduct_ShouldRejectDuplicate() {
        ProductDTO dto = productDTO();
        when(productRepository.existsByNameAndCategoryId("Laptop", 1L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> productService.createProduct(dto));

        assertEquals("Product name already exists in this category", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void getAllProducts_ShouldReturnMappedPage() {
        Pageable pageable = mock(Pageable.class);
        Product product = product();
        ProductResponseDTO response = new ProductResponseDTO();

        when(productRepository.findByIsActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productMapper.toResponseDTO(product)).thenReturn(response);

        Page<ProductResponseDTO> result = productService.getAllProducts(pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().get(0));
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        Product product = product();
        ProductResponseDTO response = new ProductResponseDTO();

        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(response);

        assertSame(response, productService.getProductById(1L));
    }

    @Test
    void getProductById_ShouldThrowWhenMissing() {
        when(productRepository.findByIdAndIsActiveTrue(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(99L));

        assertEquals("Product not found with id: 99", ex.getMessage());
    }

    @Test
    void getProductsByCategory_ShouldReturnMappedPage() {
        Pageable pageable = mock(Pageable.class);
        Product product = product();
        ProductResponseDTO response = new ProductResponseDTO();

        when(productRepository.findByCategoryIdAndIsActiveTrue(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productMapper.toResponseDTO(product)).thenReturn(response);

        Page<ProductResponseDTO> result =
                productService.getProductsByCategory(1L, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().get(0));
    }

    @Test
    void getFeaturedProducts_ShouldReturnMappedPage() {
        Pageable pageable = mock(Pageable.class);
        Product product = product();
        ProductResponseDTO response = new ProductResponseDTO();

        when(productRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productMapper.toResponseDTO(product)).thenReturn(response);

        Page<ProductResponseDTO> result =
                productService.getFeaturedProducts(pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().get(0));
    }

    @Test
    void updateProduct_ShouldUpdateProvidedFields() {
        Product product = product();
        ProductDTO dto = productDTO();
        ProductResponseDTO response = new ProductResponseDTO();

        dto.setName(" Updated Laptop ");
        dto.setCategoryId(2L);
        dto.setDescription(" Updated description ");
        dto.setSku(" SKU-002 ");
        dto.setBrand(" NewBrand ");
        dto.setModel(" NewModel ");
        dto.setSpecifications(Map.of("ram", "16GB"));
        dto.setImages(List.of("one.jpg", "two.jpg"));
        dto.setIsFeatured(true);

        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndCategoryIdAndIdNot(
                "Updated Laptop", 2L, 1L)).thenReturn(false);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(response);

        ProductResponseDTO result = productService.updateProduct(1L, dto);

        assertSame(response, result);
        assertEquals("Updated Laptop", product.getName());
        assertEquals(2L, product.getCategoryId());
        assertEquals("Updated description", product.getDescription());
        assertEquals("SKU-002", product.getSku());
        assertEquals("NewBrand", product.getBrand());
        assertEquals("NewModel", product.getModel());
        assertEquals(Map.of("ram", "16GB"), product.getSpecifications());
        assertEquals(List.of("one.jpg", "two.jpg"), product.getImages());
        assertTrue(product.isFeatured());
        verify(roleVerificationService).verifyAdminOrSuperAdminRole();
    }

    @Test
    void updateProduct_ShouldRejectNullDto() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(1L, null));

        assertEquals("Update data cannot be null", ex.getMessage());
    }

    @Test
    void updateProduct_ShouldThrowWhenMissing() {
        ProductDTO dto = new ProductDTO();
        when(productRepository.findByIdAndIsActiveTrue(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, dto));
    }

    @Test
    void updateProduct_ShouldRejectBlankName() {
        ProductDTO dto = new ProductDTO();
        dto.setName("   ");
        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(1L, dto));

        assertEquals("Product name cannot be empty", ex.getMessage());
    }

    @Test
    void updateProduct_ShouldRejectDuplicateName() {
        ProductDTO dto = new ProductDTO();
        dto.setName("New Laptop");
        Product product = product();

        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndCategoryIdAndIdNot(
                "New Laptop", 1L, 1L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> productService.updateProduct(1L, dto));
    }

    @Test
    void updateProduct_ShouldRejectCategoryConflict() {
        ProductDTO dto = new ProductDTO();
        dto.setCategoryId(2L);
        Product product = product();

        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndCategoryIdAndIdNot(
                "Laptop", 2L, 1L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> productService.updateProduct(1L, dto));
    }

    @Test
    void updateProduct_ShouldRejectLongDescription() {
        ProductDTO dto = new ProductDTO();
        dto.setDescription("a".repeat(2001));
        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product()));

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateProduct(1L, dto));
    }

    @Test
    void updateProduct_ShouldRejectInvalidSpecification() {
        ProductDTO dto = new ProductDTO();
        Map<String, String> specs = new HashMap<>();
        specs.put(" ", "value");
        dto.setSpecifications(specs);

        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product()));

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateProduct(1L, dto));
    }

    @Test
    void updateProduct_ShouldRejectLongImageUrl() {
        ProductDTO dto = new ProductDTO();
        dto.setImages(List.of("x".repeat(256)));

        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product()));

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateProduct(1L, dto));
    }

    @Test
    void toggleFeaturedStatus_ShouldToggleAndSave() {
        Product product = product();
        ProductResponseDTO response = new ProductResponseDTO();

        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(response);

        ProductResponseDTO result = productService.toggleFeaturedStatus(1L);

        assertSame(response, result);
        assertTrue(product.isFeatured());
        verify(roleVerificationService).verifyAdminOrSuperAdminRole();
    }

    @Test
    void deleteProduct_ShouldSoftDelete() {
        Product product = product();
        when(productRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.deleteProduct(1L);

        assertFalse(product.isActive());
        verify(productRepository).save(product);
        verify(roleVerificationService).verifyAdminOrSuperAdminRole();
    }

    @Test
    void deleteProduct_ShouldThrowWhenMissing() {
        when(productRepository.findByIdAndIsActiveTrue(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(99L));

        verify(productRepository, never()).save(any());
    }

    private ProductDTO productDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Laptop");
        dto.setDescription("Gaming laptop");
        dto.setCategoryId(1L);
        dto.setSku("SKU-001");
        dto.setBrand("Brand");
        dto.setModel("Model");
        dto.setSpecifications(Map.of("ram", "8GB"));
        dto.setImages(List.of("image.jpg"));
        dto.setIsFeatured(false);
        return dto;
    }

    private Product product() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setCategoryId(1L);
        product.setSku("SKU-001");
        product.setBrand("Brand");
        product.setModel("Model");
        product.setSpecifications(new HashMap<>(Map.of("ram", "8GB")));
        product.setImages(new ArrayList<>(List.of("image.jpg")));
        product.setActive(true);
        product.setFeatured(false);
        return product;
    }
}
