package com.mankind.matrix_product_service.controller;

import com.mankind.api.product.dto.product.ProductResponseDTO;
import com.mankind.api.product.dto.supplier.SupplierDTO;
import com.mankind.api.product.dto.supplier.SupplierResponseDTO;
import com.mankind.api.product.dto.supplier.SupplierDashboardDTO;
import com.mankind.matrix_product_service.service.SupplierService;
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
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "APIs for managing suppliers")
public class SupplierController {
    private static final Logger log = LoggerFactory.getLogger(SupplierController.class);
    private final SupplierService supplierService;

    @Operation(summary = "Create a new supplier", description = "Creates a new supplier in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Supplier successfully created",
                    content = @Content(schema = @Schema(implementation = SupplierResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Supplier name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(
            @Parameter(description = "Supplier object to be created", required = true)
            @Valid @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createSupplier(supplierDTO));
    }

    @Operation(summary = "Get all suppliers", description = "Retrieves a paginated list of all active suppliers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved suppliers",
                    content = @Content(schema = @Schema(implementation = SupplierResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<SupplierResponseDTO>> getAllSuppliers(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(pageable));
    }

    @Operation(summary = "Get supplier by ID", description = "Retrieves a specific supplier by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved supplier",
                    content = @Content(schema = @Schema(implementation = SupplierResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(
            @Parameter(description = "ID of the supplier to retrieve", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @Operation(summary = "Get products supplied by supplier", description = "Retrieves a paginated list of active products supplied by the given supplier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/products")
    public ResponseEntity<Page<ProductResponseDTO>> getSupplierProducts(
            @Parameter(description = "ID of the supplier", required = true)
            @PathVariable Long id,
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        return ResponseEntity.ok(supplierService.getSupplierProducts(id, pageable));
    }

    @Operation(summary = "Update supplier", description = "Updates an existing supplier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier successfully updated",
                    content = @Content(schema = @Schema(implementation = SupplierResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "409", description = "Supplier name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(
            @Parameter(description = "ID of the supplier to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated supplier object", required = true)
            @Valid @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDTO));
    }

    @Operation(summary = "Delete supplier", description = "Soft deletes a supplier by marking it as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Supplier successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "ID of the supplier to delete", required = true)
            @PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get supplier dashboard", description = "Retrieves dashboard metrics for a supplier including product counts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved dashboard",
                content = @Content(schema = @Schema(implementation = SupplierDashboardDTO.class))),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<SupplierDashboardDTO> getSupplierDashboard(
            @Parameter(description = "ID of the supplier to retrieve dashboard for", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierDashboard(id));
    }
}