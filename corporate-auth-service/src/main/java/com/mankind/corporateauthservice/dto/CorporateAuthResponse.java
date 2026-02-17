package com.mankind.corporateauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "CorporateAuthResponse", description = "Authentication response with JWT token and corporate user details")
public class CorporateAuthResponse {
    @Schema(description = "Operation result message", example = "Corporate login successful")
    private String message;
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "Token validity period in seconds", example = "3600")
    private long expiresIn;
    @Schema(description = "Authenticated corporate user details")
    private CorporateUserResponse user;
}
