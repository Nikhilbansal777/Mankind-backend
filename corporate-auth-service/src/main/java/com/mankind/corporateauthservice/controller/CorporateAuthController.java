package com.mankind.corporateauthservice.controller;

import com.mankind.corporateauthservice.dto.CorporateAuthResponse;
import com.mankind.corporateauthservice.dto.CorporateLoginRequest;
import com.mankind.corporateauthservice.dto.CorporateRegistrationRequest;
import com.mankind.corporateauthservice.service.CorporateAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/corporate/auth")
public class CorporateAuthController {

    private final CorporateAuthService corporateAuthService;

    public CorporateAuthController(CorporateAuthService corporateAuthService) {
        this.corporateAuthService = corporateAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<CorporateAuthResponse> register(@Valid @RequestBody CorporateRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corporateAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<CorporateAuthResponse> login(@Valid @RequestBody CorporateLoginRequest request) {
        return ResponseEntity.ok(corporateAuthService.login(request));
    }
}
