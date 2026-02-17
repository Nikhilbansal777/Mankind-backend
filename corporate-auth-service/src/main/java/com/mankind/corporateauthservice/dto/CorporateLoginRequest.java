package com.mankind.corporateauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "CorporateLoginRequest", description = "Corporate login request payload")
public class CorporateLoginRequest {

    @NotBlank
    @Email
    @Pattern(
            regexp = "^(?i)(?!.*@(gmail|yahoo)\\.com$).+$",
            message = "Only corporate email addresses are allowed"
    )
    @Schema(description = "Corporate email address", example = "admin@acme.com")
    private String email;

    @NotBlank
    @Schema(description = "Account password", example = "SecurePassword123!")
    private String password;
}
