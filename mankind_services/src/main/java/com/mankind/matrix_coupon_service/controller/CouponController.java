package com.mankind.matrix_coupon_service.controller;

import com.mankind.matrix_coupon_service.dto.CreateCouponRequest;
import com.mankind.matrix_coupon_service.dto.UseCouponRequest;
import com.mankind.matrix_coupon_service.model.Coupon;
import com.mankind.matrix_coupon_service.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // ==================== USER OPERATIONS ====================

    @Operation(summary = "Get all active coupons", description = "Retrieves a paginated list of all active coupons (no date validation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved coupons",
                    content = @Content(schema = @Schema(implementation = Coupon.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Tag(name = "User Operations", description = "APIs for users to view and use coupons")
    @GetMapping
    public ResponseEntity<Page<Coupon>> getAllActiveCoupons(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        return ResponseEntity.ok(couponService.getAllActiveCoupons(pageable));
    }

    @Operation(summary = "Get coupon by ID", description = "Retrieves a specific coupon by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved coupon",
                    content = @Content(schema = @Schema(implementation = Coupon.class))),
        @ApiResponse(responseCode = "404", description = "Coupon not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Tag(name = "User Operations", description = "APIs for users to view and use coupons")
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(
            @Parameter(description = "ID of the coupon to retrieve", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @Operation(summary = "Validate coupon code", description = "Validates a coupon code for the current user with date and usage validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coupon is valid"),
        @ApiResponse(responseCode = "400", description = "Coupon is invalid or expired"),
        @ApiResponse(responseCode = "404", description = "Coupon not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Tag(name = "User Operations", description = "APIs for users to view and use coupons")
    @GetMapping("/validate")
    public ResponseEntity<Coupon> validateCoupon(
            @Parameter(description = "Coupon code to validate", required = true)
            @RequestParam String code) {
        return ResponseEntity.ok(couponService.validateCoupon(code));
    }

    @Operation(summary = "Use coupon", description = "Marks a coupon as used for a specific user with optional order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coupon successfully used"),
        @ApiResponse(responseCode = "400", description = "Coupon is invalid or already used"),
        @ApiResponse(responseCode = "404", description = "Coupon not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Tag(name = "User Operations", description = "APIs for users to view and use coupons")
    @PostMapping("/use")
    public ResponseEntity<Coupon> useCoupon(
            @Parameter(description = "Use coupon request", required = true)
            @Valid @RequestBody UseCouponRequest request) {
        return ResponseEntity.ok(couponService.useCoupon(request));
    }

    // ==================== ADMIN OPERATIONS ====================

    @Operation(summary = "Create new coupon", description = "Creates a new coupon (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Coupon successfully created",
                    content = @Content(schema = @Schema(implementation = Coupon.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "409", description = "Coupon code already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Tag(name = "Admin Operations", description = "APIs for administrators to manage coupons")
    @PostMapping
    public ResponseEntity<Coupon> createCoupon(
            @Parameter(description = "Coupon details", required = true)
            @Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.createCoupon(request));
    }

    @Operation(summary = "Update coupon", description = "Updates an existing coupon with same fields as POST (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coupon successfully updated",
                    content = @Content(schema = @Schema(implementation = Coupon.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "Coupon not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Tag(name = "Admin Operations", description = "APIs for administrators to manage coupons")
    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(
            @Parameter(description = "ID of the coupon to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated coupon object", required = true)
            @Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    @Operation(summary = "Deactivate coupon", description = "Deactivates a coupon by setting isActive to false (soft delete) (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Coupon successfully deactivated"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "Coupon not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Tag(name = "Admin Operations", description = "APIs for administrators to manage coupons")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(
            @Parameter(description = "ID of the coupon to deactivate", required = true)
            @PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
} 