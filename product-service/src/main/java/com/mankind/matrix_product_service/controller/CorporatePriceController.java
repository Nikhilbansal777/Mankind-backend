package com.mankind.matrix_product_service.controller;

import com.mankind.api.product.dto.corporate.CorporatePriceDTO;
import com.mankind.api.product.dto.corporate.CorporatePriceResponseDTO;
import com.mankind.matrix_product_service.service.CorporatePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/corporates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Corporate Pricing", description = "APIs for managing corporate-specific product prices")
@SecurityRequirement(name = "bearerAuth")
public class CorporatePriceController {
    private final CorporatePriceService corporatePriceService;

    @Operation(summary = "Update a corporate price", description = "Updates or creates a price for a specific product for a corporate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Price successfully updated",
                    content = @Content(schema = @Schema(implementation = CorporatePriceResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this corporate"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{corpId}/prices/{productId}")
    public ResponseEntity<CorporatePriceResponseDTO> updatePrice(
            @Parameter(description = "Corporate ID", required = true) @PathVariable Long corpId,
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @Parameter(description = "Price details", required = true) @Valid @RequestBody CorporatePriceDTO priceDTO) {
        
        // Verify the user has access to this corporate
        verifyUserHasAccessToCorporate(corpId);
        
        // Get the username from the JWT token
        String username = getCurrentUsername();
        
        return ResponseEntity.ok(corporatePriceService.updatePrice(corpId, productId, priceDTO, username));
    }

    @Operation(summary = "Get a corporate price", description = "Retrieves the current price for a specific product for a corporate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Price successfully retrieved",
                    content = @Content(schema = @Schema(implementation = CorporatePriceResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this corporate"),
        @ApiResponse(responseCode = "404", description = "Price not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{corpId}/prices/{productId}")
    public ResponseEntity<CorporatePriceResponseDTO> getPrice(
            @Parameter(description = "Corporate ID", required = true) @PathVariable Long corpId,
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId) {
        
        // Verify the user has access to this corporate
        verifyUserHasAccessToCorporate(corpId);
        
        return ResponseEntity.ok(corporatePriceService.getPriceByCorpIdAndProductId(corpId, productId));
    }

    @Operation(summary = "Get all corporate prices", description = "Retrieves all current prices for a corporate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prices successfully retrieved",
                    content = @Content(schema = @Schema(implementation = CorporatePriceResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this corporate"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{corpId}/prices")
    public ResponseEntity<List<CorporatePriceResponseDTO>> getAllPrices(
            @Parameter(description = "Corporate ID", required = true) @PathVariable Long corpId) {
        
        // Verify the user has access to this corporate
        verifyUserHasAccessToCorporate(corpId);
        
        return ResponseEntity.ok(corporatePriceService.getAllPricesByCorpId(corpId));
    }

    /**
     * Verify that the current user has access to the specified corporate
     * 
     * @param corpId the corporate ID to check
     * @throws org.springframework.security.access.AccessDeniedException if the user doesn't have access
     */
    private void verifyUserHasAccessToCorporate(Long corpId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            // Get the corporate IDs from the JWT claims
            List<String> corporateIds = jwt.getClaimAsStringList("corporate_ids");
            if (corporateIds == null || !corporateIds.contains(corpId.toString())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "User not authorized to access corporate ID: " + corpId);
            }
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Authentication required");
        }
    }

    /**
     * Get the username from the current authentication
     * 
     * @return the username
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return "system";
    }
}