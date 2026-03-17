package com.mankind.matrix_referral_service.controller;

import com.mankind.matrix_referral_service.dto.ReferralCodeResponse;
import com.mankind.matrix_referral_service.dto.ReferralValidationResponse;
import com.mankind.matrix_referral_service.service.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/referrals")
@RequiredArgsConstructor
@Tag(name = "Referral", description = "Referral code management for authenticated users")
@SecurityRequirement(name = "bearerAuth")
public class ReferralController {

    private final ReferralService referralService;

    @Operation(summary = "Create referral code for current user", description = "Creates a referral code if not present, otherwise returns existing active code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Referral code created/retrieved",
                    content = @Content(schema = @Schema(implementation = ReferralCodeResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/me/code")
    public ResponseEntity<ReferralCodeResponse> createReferralCodeForCurrentUser() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(referralService.createOrGetCurrentUserReferralCode());
    }

    @Operation(summary = "Get current user's referral code", description = "Returns the current user's active referral code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Referral code fetched",
                    content = @Content(schema = @Schema(implementation = ReferralCodeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Referral code not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me/code")
    public ResponseEntity<ReferralCodeResponse> getCurrentUserReferralCode() {
        return ResponseEntity.ok(referralService.getCurrentUserReferralCode());
    }

    @Operation(summary = "Validate referral code", description = "Checks if a referral code exists and is active.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation result",
                    content = @Content(schema = @Schema(implementation = ReferralValidationResponse.class)))
    })
    @GetMapping("/validate")
    public ResponseEntity<ReferralValidationResponse> validateReferralCode(
            @Parameter(description = "Referral code to validate", required = true)
            @RequestParam String code) {
        return ResponseEntity.ok(new ReferralValidationResponse(referralService.validateReferralCode(code)));
    }
}
