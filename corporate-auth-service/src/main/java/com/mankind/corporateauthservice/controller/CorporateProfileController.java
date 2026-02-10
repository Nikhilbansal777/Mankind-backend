package com.mankind.corporateauthservice.controller;

import com.mankind.corporateauthservice.dto.CorporateUserResponse;
import com.mankind.corporateauthservice.model.CorporateUser;
import com.mankind.corporateauthservice.repository.CorporateUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/corporate/profile")
public class CorporateProfileController {

    private final CorporateUserRepository corporateUserRepository;

    public CorporateProfileController(CorporateUserRepository corporateUserRepository) {
        this.corporateUserRepository = corporateUserRepository;
    }

    @GetMapping
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
