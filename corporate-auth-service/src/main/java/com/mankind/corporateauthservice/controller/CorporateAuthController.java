package com.mankind.corporateauthservice.controller;

import com.mankind.corporateauthservice.dto.CorporateAuthResponse;
import com.mankind.corporateauthservice.dto.CorporateLoginRequest;
import com.mankind.corporateauthservice.dto.CorporateRegistrationRequest;
import com.mankind.corporateauthservice.dto.ErrorResponse;
import com.mankind.corporateauthservice.service.CorporateAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/corporate/auth")
@Tag(name = "Corporate Auth", description = "Corporate registration and login APIs")
public class CorporateAuthController {

    private final CorporateAuthService corporateAuthService;

    public CorporateAuthController(CorporateAuthService corporateAuthService) {
        this.corporateAuthService = corporateAuthService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register corporate user", description = "Creates a new corporate account and returns access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Corporate user registered",
                    content = @Content(schema = @Schema(implementation = CorporateAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CorporateAuthResponse> register(@Valid @RequestBody CorporateRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corporateAuthService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login corporate user", description = "Authenticates by corporate email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Corporate login successful",
                    content = @Content(schema = @Schema(implementation = CorporateAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CorporateAuthResponse> login(@Valid @RequestBody CorporateLoginRequest request) {
        return ResponseEntity.ok(corporateAuthService.login(request));
    }
}
