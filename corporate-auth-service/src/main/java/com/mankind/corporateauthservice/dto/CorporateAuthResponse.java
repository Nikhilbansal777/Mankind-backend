package com.mankind.corporateauthservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CorporateAuthResponse {
    private String message;
    private String accessToken;
    private long expiresIn;
    private CorporateUserResponse user;
}
