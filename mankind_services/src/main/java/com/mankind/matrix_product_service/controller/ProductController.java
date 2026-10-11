package com.mankind.matrix_product_service.controller;


import com.mankind.api.product.dto.product.ProductDTO;
import com.mankind.api.product.dto.product.ProductResponseDTO;
import com.mankind.matrix_product_service.service.ProductService;
import com.mankind.matrix_product_service.client.UserClient;
import com.mankind.api.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final UserClient userClient;

    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product successfully created",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "409", description = "Product name already exists in category"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Parameter(description = "Product object to be created", required = true)
            @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productDTO));
    }

    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all active products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Get products by category", description = "Retrieves a paginated list of products in a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByCategory(
            @Parameter(description = "ID of the category", required = true)
            @PathVariable Long categoryId,
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @Operation(summary = "Get featured products", description = "Retrieves a paginated list of featured products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved featured products",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/featured")
    public ResponseEntity<Page<ProductResponseDTO>> getFeaturedProducts(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        return ResponseEntity.ok(productService.getFeaturedProducts(pageable));
    }

    @Operation(summary = "Toggle featured status", description = "Toggles the featured status of a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully toggled featured status",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/featured")
    public ResponseEntity<ProductResponseDTO> toggleFeaturedStatus(
            @Parameter(description = "ID of the product to toggle featured status", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleFeaturedStatus(id));
    }

    @Operation(summary = "Update product", description = "Updates an existing product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Product or category not found"),
        @ApiResponse(responseCode = "409", description = "Product name already exists in category"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID of the product to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated product object", required = true)
            @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @Operation(summary = "Delete product", description = "Soft deletes a product by marking it as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true)
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


}
