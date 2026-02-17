package com.mankind.corporateauthservice.controller;

import com.mankind.corporateauthservice.dto.CorporateUserResponse;
import com.mankind.corporateauthservice.dto.ErrorResponse;
import com.mankind.corporateauthservice.model.CorporateUser;
import com.mankind.corporateauthservice.repository.CorporateUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/corporate/profile")
@Tag(name = "Corporate Profile", description = "Corporate profile APIs")
public class CorporateProfileController {

    private final CorporateUserRepository corporateUserRepository;

    public CorporateProfileController(CorporateUserRepository corporateUserRepository) {
        this.corporateUserRepository = corporateUserRepository;
    }

    @GetMapping
    @Operation(
            summary = "Get corporate profile",
            description = "Returns profile details for authenticated corporate user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully",
                    content = @Content(schema = @Schema(implementation = CorporateUserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CorporateUserResponse> getProfile(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        CorporateUser user = corporateUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CorporateUserResponse response = CorporateUserResponse.builder()
                .id(user.getId())
                .corporateName(user.getCorporateName())
                .email(user.getEmail())
                .dateOfJoining(user.getDateOfJoining())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return ResponseEntity.ok(response);
    }
}
