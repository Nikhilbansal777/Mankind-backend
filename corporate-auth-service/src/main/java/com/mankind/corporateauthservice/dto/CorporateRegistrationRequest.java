package com.mankind.corporateauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(name = "CorporateRegistrationRequest", description = "Corporate registration request payload")
public class CorporateRegistrationRequest {
    @NotBlank
    @Schema(description = "Corporate account display name", example = "Acme Corp")
    private String corporateName;

    @NotBlank
    @Email
    @Pattern(
            regexp = "^(?i)(?!.*@(gmail|yahoo)\\.com$).+$",
            message = "Only corporate email addresses are allowed"
    )
    @Schema(description = "Corporate email address", example = "admin@acme.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 16)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,16}$",
            message = "Password must include at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    @Schema(description = "Password with 8-16 chars and complexity requirements", example = "SecurePassword123!")
    private String password;

    @NotNull
    @PastOrPresent
    @Schema(description = "Date user joined the company", example = "2024-01-15")
    private LocalDate dateOfJoining;
}
