package com.mankind.matrix_product_service.controller;

import com.mankind.api.product.dto.corporateaccount.CorporateAccountDTO;
import com.mankind.api.product.dto.corporateaccount.CorporateAccountResponseDTO;
import com.mankind.matrix_product_service.service.CorporateAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/corporate")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Corporate Account Management", description = "APIs for managing corporate accounts")
public class CorporateAccountController {
    private final CorporateAccountService corporateAccountService;

    @Operation(summary = "Create a new corporate account", description = "Creates a new corporate account in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Corporate account successfully created",
                    content = @Content(schema = @Schema(implementation = CorporateAccountResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Corporate account name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<CorporateAccountResponseDTO> createCorporateAccount(
            @Parameter(description = "Corporate account object to be created", required = true)
            @Valid @RequestBody CorporateAccountDTO corporateAccountDTO) {
        log.info("Creating new corporate account: {}", corporateAccountDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(corporateAccountService.createCorporateAccount(corporateAccountDTO));
    }

    @Operation(summary = "Get all corporate accounts", description = "Retrieves a paginated list of all active corporate accounts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved corporate accounts",
                    content = @Content(schema = @Schema(implementation = CorporateAccountResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<CorporateAccountResponseDTO>> getAllCorporateAccounts(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        log.info("Fetching all corporate accounts with pagination");
        return ResponseEntity.ok(corporateAccountService.getAllCorporateAccounts(pageable));
    }

    @Operation(summary = "Get corporate account by ID", description = "Retrieves a specific corporate account by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved corporate account",
                    content = @Content(schema = @Schema(implementation = CorporateAccountResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Corporate account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CorporateAccountResponseDTO> getCorporateAccountById(
            @Parameter(description = "ID of the corporate account to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Fetching corporate account with ID: {}", id);
        return ResponseEntity.ok(corporateAccountService.getCorporateAccountById(id));
    }

    @Operation(summary = "Update corporate account", description = "Updates an existing corporate account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Corporate account successfully updated",
                    content = @Content(schema = @Schema(implementation = CorporateAccountResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Corporate account not found"),
        @ApiResponse(responseCode = "409", description = "Corporate account name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CorporateAccountResponseDTO> updateCorporateAccount(
            @Parameter(description = "ID of the corporate account to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated corporate account object", required = true)
            @Valid @RequestBody CorporateAccountDTO corporateAccountDTO) {
        log.info("Updating corporate account with ID: {}", id);
        return ResponseEntity.ok(corporateAccountService.updateCorporateAccount(id, corporateAccountDTO));
    }

    @Operation(summary = "Delete corporate account", description = "Soft deletes a corporate account by marking it as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Corporate account successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Corporate account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCorporateAccount(
            @Parameter(description = "ID of the corporate account to delete", required = true)
            @PathVariable Long id) {
        log.info("Deleting corporate account with ID: {}", id);
        corporateAccountService.deleteCorporateAccount(id);
        return ResponseEntity.noContent().build();
    }
}
